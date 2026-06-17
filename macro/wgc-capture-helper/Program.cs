using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;
using System.Threading.Tasks;
using Windows.Graphics.Capture;
using Windows.Graphics.DirectX;
using Windows.Graphics.DirectX.Direct3D11;
using Windows.Graphics.Imaging;
using Windows.Storage.Streams;

namespace VArchive.WgcCapture;

internal static class Program
{
    private const string ExitCommand = "exit";
    private const string ServerCommand = "--server";
    private const string SizeCommand = "size";
    private const string ToneMapCommand = "--tone-map";

    private static async Task<int> Main(string[] args)
    {
        try
        {
            if (!GraphicsCaptureSession.IsSupported())
            {
                throw new InvalidOperationException("Windows Graphics Capture is not supported.");
            }

            bool toneMap = args.Length > 0 && Array.IndexOf(args, ToneMapCommand) >= 0;
            string[] positionalArgs = Array.FindAll(args, x => x != ToneMapCommand);

            if (positionalArgs.Length == 1 && positionalArgs[0] == ServerCommand)
            {
                await RunServerAsync(toneMap);
            }
            else if (positionalArgs.Length == 1)
            {
                await using CaptureHost host = CaptureHost.CreatePrimaryMonitor(toneMap);
                await host.CaptureAsync(Path.GetFullPath(positionalArgs[0]), toneMap);
            }
            else
            {
                Console.Error.WriteLine("Usage: VArchive.WgcCapture.exe [--tone-map] --server");
                Console.Error.WriteLine("Usage: VArchive.WgcCapture.exe [--tone-map] <output.bmp>");
                return 2;
            }

            return 0;
        }
        catch (Exception e)
        {
            Console.Error.WriteLine(e);
            return 1;
        }
    }

    private static async Task RunServerAsync(bool toneMap)
    {
        await using CaptureHost host = CaptureHost.CreatePrimaryMonitor(toneMap);

        Console.WriteLine("READY");
        Console.Out.Flush();

        string? line;
        while ((line = Console.ReadLine()) != null)
        {
            if (line == ExitCommand)
            {
                break;
            }
            if (line == SizeCommand)
            {
                Console.WriteLine($"SIZE {host.Width} {host.Height}");
                Console.Out.Flush();
                continue;
            }

            try
            {
                await host.CaptureAsync(Path.GetFullPath(line), toneMap);
                Console.WriteLine("OK");
            }
            catch (Exception e)
            {
                Console.WriteLine("ERR " + e.Message.ReplaceLineEndings(" "));
            }

            Console.Out.Flush();
        }
    }

    private sealed class CaptureHost : IAsyncDisposable
    {
        private const int RgbaFloatBytesPerPixel = 8;
        private const int RgbaFloatBlueOffset = 4;
        private const int RgbaFloatGreenOffset = 2;
        private const int RgbaFloatRedOffset = 0;
        private const int BgraBytesPerPixel = 4;
        private const int BgraBlueOffset = 0;
        private const int BgraGreenOffset = 1;
        private const int BgraRedOffset = 2;
        private const int BgraAlphaOffset = 3;
        private const int BitmapFileHeaderSize = 14;
        private const int BitmapInfoHeaderSize = 40;
        private const short BitmapPlanes = 1;
        private const short BitmapBitsPerPixel = 24;
        private const int BitmapCompressionRgb = 0;
        private const int BgrBytesPerPixel = 3;
        private static readonly TimeSpan CachedFrameWaitTimeout = TimeSpan.FromMilliseconds(20);
        private const float Exposure = 0.55f;
        private const float Saturation = 0.82f;
        private const float Contrast = 1.08f;
        private const float Gamma = 2.2f;
        private const float Half = 0.5f;
        private const byte OpaqueAlpha = 255;
        private const float RedWeight = 0.2126f;
        private const float GreenWeight = 0.7152f;
        private const float BlueWeight = 0.0722f;

        private readonly GraphicsCaptureItem item;
        private readonly IDirect3DDevice device;
        private readonly Direct3D11CaptureFramePool framePool;
        private readonly GraphicsCaptureSession session;
        private readonly object frameLock = new();
        private Direct3D11CaptureFrame? latestFrame;

        private CaptureHost(GraphicsCaptureItem item, IDirect3DDevice device, bool toneMap)
        {
            this.item = item;
            this.device = device;

            DirectXPixelFormat pixelFormat = toneMap
                    ? DirectXPixelFormat.R16G16B16A16Float
                    : DirectXPixelFormat.B8G8R8A8UIntNormalized;
            framePool = Direct3D11CaptureFramePool.CreateFreeThreaded(device, pixelFormat, 2,
                    item.Size);
            framePool.FrameArrived += OnFrameArrived;
            session = framePool.CreateCaptureSession(item);
            session.IsCursorCaptureEnabled = false;
            session.StartCapture();
        }

        public int Width => item.Size.Width;

        public int Height => item.Size.Height;

        public static CaptureHost CreatePrimaryMonitor(bool toneMap)
        {
            IntPtr monitor = NativeMethods.MonitorFromPoint(new NativeMethods.Point(), 1);
            GraphicsCaptureItem item = NativeMethods.CreateItemForMonitor(monitor);
            IDirect3DDevice device = NativeMethods.CreateDirect3DDevice();

            return new CaptureHost(item, device, toneMap);
        }

        public async Task CaptureAsync(string outputPath, bool toneMap)
        {
            using Direct3D11CaptureFrame? frame = await TakeLatestFrameAsync();
            if (frame == null)
            {
                await CaptureSingleFrameAsync(outputPath, toneMap);
                return;
            }

            await SaveFrameAsync(frame, outputPath, toneMap);
        }

        private async Task SaveFrameAsync(Direct3D11CaptureFrame frame, string outputPath,
                bool toneMap)
        {
            if (toneMap)
            {
                await SaveToneMappedSurfaceAsync(frame.Surface, outputPath);
            }
            else
            {
                await SaveSurfaceAsync(frame.Surface, outputPath);
            }
        }

        private void OnFrameArrived(Direct3D11CaptureFramePool sender, object args)
        {
            Direct3D11CaptureFrame frame = sender.TryGetNextFrame();
            lock (frameLock)
            {
                latestFrame?.Dispose();
                latestFrame = frame;
                Monitor.PulseAll(frameLock);
            }
        }

        private Task<Direct3D11CaptureFrame?> TakeLatestFrameAsync()
        {
            lock (frameLock)
            {
                if (latestFrame == null)
                {
                    Monitor.Wait(frameLock, CachedFrameWaitTimeout);
                }

                Direct3D11CaptureFrame? frame = latestFrame;
                latestFrame = null;

                return Task.FromResult(frame);
            }
        }

        private async Task CaptureSingleFrameAsync(string outputPath, bool toneMap)
        {
            DirectXPixelFormat pixelFormat = toneMap
                    ? DirectXPixelFormat.R16G16B16A16Float
                    : DirectXPixelFormat.B8G8R8A8UIntNormalized;
            using Direct3D11CaptureFramePool fallbackFramePool =
                    Direct3D11CaptureFramePool.CreateFreeThreaded(device, pixelFormat, 2,
                            item.Size);
            using GraphicsCaptureSession fallbackSession =
                    fallbackFramePool.CreateCaptureSession(item);
            fallbackSession.IsCursorCaptureEnabled = false;

            Task<Direct3D11CaptureFrame> nextFrame = NextFrameAsync(fallbackFramePool);
            fallbackSession.StartCapture();

            using Direct3D11CaptureFrame frame = await nextFrame;
            await SaveFrameAsync(frame, outputPath, toneMap);
        }

        private static Task<Direct3D11CaptureFrame> NextFrameAsync(
                Direct3D11CaptureFramePool framePool)
        {
            TaskCompletionSource<Direct3D11CaptureFrame> taskSource = new();

            void Handler(Direct3D11CaptureFramePool sender, object args)
            {
                sender.FrameArrived -= Handler;
                taskSource.TrySetResult(sender.TryGetNextFrame());
            }

            framePool.FrameArrived += Handler;

            return taskSource.Task.WaitAsync(TimeSpan.FromSeconds(5));
        }

        private async Task SaveToneMappedSurfaceAsync(IDirect3DSurface surface, string outputPath)
        {
            using SoftwareBitmap bitmap = await SoftwareBitmap.CreateCopyFromSurfaceAsync(surface);
            if (bitmap.BitmapPixelFormat != BitmapPixelFormat.Rgba16)
            {
                throw new InvalidOperationException(
                        "Unexpected HDR capture format: " + bitmap.BitmapPixelFormat);
            }

            byte[] pixels = ToneMapRgba16(bitmap);
            await SaveBgra8BmpAsync(pixels, bitmap.PixelWidth, bitmap.PixelHeight, outputPath);
        }

        private static async Task SaveSurfaceAsync(IDirect3DSurface surface, string outputPath)
        {
            using SoftwareBitmap bitmap = await SoftwareBitmap.CreateCopyFromSurfaceAsync(surface);
            using SoftwareBitmap converted =
                    SoftwareBitmap.Convert(bitmap, BitmapPixelFormat.Bgra8, BitmapAlphaMode.Ignore);

            byte[] pixels = CopyBgra8(converted);
            await SaveBgra8BmpAsync(pixels, converted.PixelWidth, converted.PixelHeight,
                    outputPath);
        }

        private static byte[] CopyBgra8(SoftwareBitmap bitmap)
        {
            byte[] pixels = new byte[bitmap.PixelWidth * bitmap.PixelHeight * BgraBytesPerPixel];
            Windows.Storage.Streams.Buffer buffer = new((uint)pixels.Length);
            bitmap.CopyToBuffer(buffer);
            DataReader.FromBuffer(buffer).ReadBytes(pixels);

            return pixels;
        }

        private static byte[] ToneMapRgba16(SoftwareBitmap bitmap)
        {
            int width = bitmap.PixelWidth;
            int height = bitmap.PixelHeight;
            byte[] output = new byte[width * height * BgraBytesPerPixel];
            byte[] source = new byte[width * height * RgbaFloatBytesPerPixel];
            Windows.Storage.Streams.Buffer sourceBuffer =
                    new((uint)(width * height * RgbaFloatBytesPerPixel));
            bitmap.CopyToBuffer(sourceBuffer);
            Windows.Storage.Streams.DataReader.FromBuffer(sourceBuffer).ReadBytes(source);

            Parallel.For(0, height, y =>
            {
                int sourceRowOffset = y * width * RgbaFloatBytesPerPixel;
                int outputRowOffset = y * width * BgraBytesPerPixel;

                for (int x = 0; x < width; x++)
                {
                    int sourceOffset = sourceRowOffset + (x * RgbaFloatBytesPerPixel);
                    float red = ReadHalf(source, sourceOffset + RgbaFloatRedOffset);
                    float green = ReadHalf(source, sourceOffset + RgbaFloatGreenOffset);
                    float blue = ReadHalf(source, sourceOffset + RgbaFloatBlueOffset);

                    ToneMapPixel(ref red, ref green, ref blue);

                    int outputOffset = outputRowOffset + (x * BgraBytesPerPixel);
                    output[outputOffset + BgraBlueOffset] = ToByte(blue);
                    output[outputOffset + BgraGreenOffset] = ToByte(green);
                    output[outputOffset + BgraRedOffset] = ToByte(red);
                    output[outputOffset + BgraAlphaOffset] = OpaqueAlpha;
                }
            });

            return output;
        }

        private static float ReadHalf(byte[] data, int offset)
        {
            ushort value = (ushort)(data[offset] | (data[offset + 1] << 8));

            return (float)BitConverter.UInt16BitsToHalf(value);
        }

        private static void ToneMapPixel(ref float red, ref float green, ref float blue)
        {
            red = Reinhard(Math.Max(0, red) * Exposure);
            green = Reinhard(Math.Max(0, green) * Exposure);
            blue = Reinhard(Math.Max(0, blue) * Exposure);

            float luminance = (red * RedWeight) + (green * GreenWeight) + (blue * BlueWeight);
            red = CompressSaturation(red, luminance);
            green = CompressSaturation(green, luminance);
            blue = CompressSaturation(blue, luminance);
        }

        private static float Reinhard(float value)
        {
            return value / (1 + value);
        }

        private static float CompressSaturation(float color, float luminance)
        {
            return luminance + ((color - luminance) * Saturation);
        }

        private static byte ToByte(float linear)
        {
            float gammaEncoded = MathF.Pow(Clamp(linear), 1 / Gamma);
            float contrasted = ((gammaEncoded - Half) * Contrast) + Half;

            return (byte)Math.Round(Clamp(contrasted) * byte.MaxValue);
        }

        private static float Clamp(float value)
        {
            return Math.Max(0, Math.Min(1, value));
        }

        private static async Task SaveBgra8BmpAsync(byte[] pixels, int width, int height,
                string outputPath)
        {
            string directory = Path.GetDirectoryName(outputPath)
                    ?? throw new ArgumentException("The output path has no directory.");
            Directory.CreateDirectory(directory);

            await using FileStream stream = new(outputPath, FileMode.Create, FileAccess.Write,
                    FileShare.Read);
            using BinaryWriter writer = new(stream);

            int rowStride = ((width * BgrBytesPerPixel) + 3) & ~3;
            int imageSize = rowStride * height;
            int fileSize = BitmapFileHeaderSize + BitmapInfoHeaderSize + imageSize;

            writer.Write((byte)'B');
            writer.Write((byte)'M');
            writer.Write(fileSize);
            writer.Write(0);
            writer.Write(BitmapFileHeaderSize + BitmapInfoHeaderSize);

            writer.Write(BitmapInfoHeaderSize);
            writer.Write(width);
            writer.Write(-height);
            writer.Write(BitmapPlanes);
            writer.Write(BitmapBitsPerPixel);
            writer.Write(BitmapCompressionRgb);
            writer.Write(imageSize);
            writer.Write(0);
            writer.Write(0);
            writer.Write(0);
            writer.Write(0);

            byte[] row = new byte[rowStride];
            for (int y = 0; y < height; y++)
            {
                Array.Clear(row);
                int sourceRowOffset = y * width * BgraBytesPerPixel;
                for (int x = 0; x < width; x++)
                {
                    int sourceOffset = sourceRowOffset + (x * BgraBytesPerPixel);
                    int targetOffset = x * BgrBytesPerPixel;
                    row[targetOffset] = pixels[sourceOffset + BgraBlueOffset];
                    row[targetOffset + 1] = pixels[sourceOffset + BgraGreenOffset];
                    row[targetOffset + 2] = pixels[sourceOffset + BgraRedOffset];
                }

                await stream.WriteAsync(row);
            }
        }

        public ValueTask DisposeAsync()
        {
            framePool.FrameArrived -= OnFrameArrived;
            session.Dispose();
            framePool.Dispose();
            latestFrame?.Dispose();
            device.Dispose();

            return ValueTask.CompletedTask;
        }
    }

    private static class NativeMethods
    {
        public static GraphicsCaptureItem CreateItemForMonitor(IntPtr monitor)
        {
            IntPtr factoryPtr = GetActivationFactory(
                    "Windows.Graphics.Capture.GraphicsCaptureItem",
                    new Guid("3628E81B-3CAC-4C60-B7F4-23CE0E0C3356"));
            IGraphicsCaptureItemInterop interop =
                    (IGraphicsCaptureItemInterop)Marshal.GetObjectForIUnknown(factoryPtr);
            Marshal.Release(factoryPtr);

            Guid itemId = new("79C3F95B-31F7-4EC2-A464-632EF5D30760");
            IntPtr itemPtr = interop.CreateForMonitor(monitor, ref itemId);

            try
            {
                return WinRT.MarshalInterface<GraphicsCaptureItem>.FromAbi(itemPtr);
            }
            finally
            {
                Marshal.Release(itemPtr);
            }
        }

        public static IDirect3DDevice CreateDirect3DDevice()
        {
            int hr = D3D11CreateDevice(IntPtr.Zero, 1, IntPtr.Zero, 0x20, IntPtr.Zero, 0, 7,
                    out IntPtr d3dDevice, out _, out IntPtr context);
            if (hr < 0)
            {
                Marshal.ThrowExceptionForHR(hr);
            }
            Marshal.Release(context);

            try
            {
                hr = CreateDirect3D11DeviceFromDXGIDevice(d3dDevice, out IntPtr inspectable);
                if (hr < 0)
                {
                    Marshal.ThrowExceptionForHR(hr);
                }

                try
                {
                    return WinRT.MarshalInterface<IDirect3DDevice>.FromAbi(inspectable);
                }
                finally
                {
                    Marshal.Release(inspectable);
                }
            }
            finally
            {
                Marshal.Release(d3dDevice);
            }
        }

        private static IntPtr GetActivationFactory(string className, Guid iid)
        {
            int hr = WindowsCreateString(className, className.Length, out IntPtr hstring);
            if (hr < 0)
            {
                Marshal.ThrowExceptionForHR(hr);
            }

            try
            {
                hr = RoGetActivationFactory(hstring, ref iid, out IntPtr factory);
                if (hr < 0)
                {
                    Marshal.ThrowExceptionForHR(hr);
                }

                return factory;
            }
            finally
            {
                WindowsDeleteString(hstring);
            }
        }

        [DllImport("user32.dll")]
        public static extern IntPtr MonitorFromPoint(Point pt, uint flags);

        [DllImport("d3d11.dll")]
        private static extern int D3D11CreateDevice(IntPtr adapter, int driverType,
                IntPtr software, uint flags, IntPtr featureLevels, uint featureLevelsCount,
                uint sdkVersion, out IntPtr device, out int featureLevel,
                out IntPtr immediateContext);

        [DllImport("d3d11.dll")]
        private static extern int CreateDirect3D11DeviceFromDXGIDevice(IntPtr dxgiDevice,
                out IntPtr graphicsDevice);

        [DllImport("api-ms-win-core-winrt-string-l1-1-0.dll", CharSet = CharSet.Unicode)]
        private static extern int WindowsCreateString(string sourceString, int length,
                out IntPtr hstring);

        [DllImport("api-ms-win-core-winrt-string-l1-1-0.dll")]
        private static extern int WindowsDeleteString(IntPtr hstring);

        [DllImport("api-ms-win-core-winrt-l1-1-0.dll")]
        private static extern int RoGetActivationFactory(IntPtr activatableClassId, ref Guid iid,
                out IntPtr factory);

        [StructLayout(LayoutKind.Sequential)]
        public struct Point
        {
            public int x;
            public int y;
        }

        [ComImport]
        [Guid("3628E81B-3CAC-4C60-B7F4-23CE0E0C3356")]
        [InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
        private interface IGraphicsCaptureItemInterop
        {
            IntPtr CreateForWindow(IntPtr window, ref Guid iid);

            IntPtr CreateForMonitor(IntPtr monitor, ref Guid iid);
        }

    }
}
