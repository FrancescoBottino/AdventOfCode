fun getFileContent(fileName: String): String {
    return object {}.javaClass.getResource(fileName)
        ?.readText()
        ?: throw RuntimeException("File not found")
}