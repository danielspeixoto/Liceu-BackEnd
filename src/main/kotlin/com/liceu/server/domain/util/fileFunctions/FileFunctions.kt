package com.liceu.server.domain.util.fileFunctions

import java.util.regex.Pattern

object FileFunctions {
    fun extractMimeType(encodedString: String?): String {
        val mime = Pattern.compile("^data:([a-zA-Z0-9]+/[a-zA-Z0-9]+).*,.*");
        val matcher = mime.matcher(encodedString);
        if (!matcher.find())
            return "";
        return matcher.group(1).toLowerCase();
    }

    fun calculateFileSize(encodedString: String): Long{
        var lastCharacters = 0
        if(encodedString.contains("==")){
            lastCharacters = 2
        }else if (encodedString.contains("=")){
            lastCharacters = 1
        }
        return ((encodedString.length*3)/4 - lastCharacters).toLong()
    }
}