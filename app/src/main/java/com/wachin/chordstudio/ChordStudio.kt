package com.wachin.chordstudio

object ChordStudio {

    private val chromatic = listOf(
        listOf("C"),
        listOf("C#", "Db"),
        listOf("D"),
        listOf("D#", "Eb"),
        listOf("E"),
        listOf("F"),
        listOf("F#", "Gb"),
        listOf("G"),
        listOf("G#", "Ab"),
        listOf("A"),
        listOf("A#", "Bb"),
        listOf("B")
    )

    // Reconoce:
    // C, Cm, Cmaj7, F#, Bb, Dsus4, G/B, F#m7/C#
    private val chordRegex = Regex(
        pattern = """\b([A-G])([#b]?)(maj|min|m|dim|aug|sus|add)?(\d{0,2})?((/[A-G][#b]?)?)\b"""
    )

    fun transposeText(text: String, semitones: Int, useSharps: Boolean): String {
        return text.lines().joinToString("\n") { line ->
            if (isChordLine(line)) {
                transposeChordLine(line, semitones, useSharps)
            } else {
                line
            }
        }
    }

    private fun isChordLine(line: String): Boolean {
        val parts = line.trim().split(Regex("""\s+""")).filter { it.isNotBlank() }
        if (parts.isEmpty()) return false

        val chordCount = parts.count { part ->
            chordRegex.matches(part)
        }

        return chordCount >= (parts.size / 2 + parts.size % 2)
    }

    private fun transposeChordLine(line: String, semitones: Int, useSharps: Boolean): String {
        return chordRegex.replace(line) { matchResult ->
            transposeChord(matchResult.value, semitones, useSharps)
        }
    }

    fun transposeChord(chord: String, semitones: Int, useSharps: Boolean): String {
        val match = chordRegex.matchEntire(chord) ?: return chord

        val root = match.groupValues[1] + match.groupValues[2]
        val quality = match.groupValues[3]
        val extension = match.groupValues[4]
        val slashPart = match.groupValues[5]

        val newRoot = transposeSingleNote(root, semitones, useSharps) ?: return chord

        val newSlash = if (slashPart.startsWith("/")) {
            val bass = slashPart.removePrefix("/")
            val newBass = transposeSingleNote(bass, semitones, useSharps) ?: bass
            "/$newBass"
        } else {
            ""
        }

        return newRoot + quality + extension + newSlash
    }

    private fun transposeSingleNote(note: String, semitones: Int, useSharps: Boolean): String? {
        val index = chromatic.indexOfFirst { enharmonics ->
            note in enharmonics
        }

        if (index == -1) return null

        val newIndex = (index + semitones).mod(chromatic.size)
        val enharmonics = chromatic[newIndex]

        return if (enharmonics.size == 1) {
            enharmonics[0]
        } else {
            if (useSharps) enharmonics[0] else enharmonics[1]
        }
    }
}
