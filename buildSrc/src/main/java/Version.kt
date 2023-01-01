object Version {
    const val ROOT_PROJECT = "1.1.0"
    const val ROOT_PROJECT_PREFIX = "dev"

    fun makeVersionString(version: String = ""): String {
        val list = mutableListOf(ROOT_PROJECT)
        if (!version.isBlank()) {
            list.add(version)
        }
        if (!ROOT_PROJECT_PREFIX.isBlank()) {
            list.add(ROOT_PROJECT_PREFIX)
        }

        return list.joinToString("-")
    }
}
