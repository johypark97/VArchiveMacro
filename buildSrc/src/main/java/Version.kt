object Version {
    private const val ROOT_PROJECT = "1.1.0"
    private const val ROOT_PROJECT_PREFIX = "dev"

    fun makeVersionString(version: String = ""): String {
        val list = mutableListOf(ROOT_PROJECT)
        if (version.isNotBlank()) {
            list.add(version)
        }
        if (ROOT_PROJECT_PREFIX.isNotBlank()) {
            list.add(ROOT_PROJECT_PREFIX)
        }

        return list.joinToString("-")
    }
}
