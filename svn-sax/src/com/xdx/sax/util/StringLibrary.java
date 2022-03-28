/**
 *  Copyright (C) 2008-2009 XDx All rights reserved.
 *
 *  @author <a href="mailto:smeier@xdx.com">Stefan Meier</a>
 */
package com.xdx.sax.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * @author scchavis
 * @author smeier
 *
 */
public final class StringLibrary {

    // FUNCTION OVERLOADING/ALIASING METHODS
    public static boolean emptyString(String inputString) {
    	return blankString(inputString);
    }

    // WORKHORSE FUNCTIONS - GENERAL USE AND HELPER FUNCTIONS

    /****************************************************************************
     * General function that acts as an alias for String.equals() and compares  *
     * two strings in order to avoid potentially calling the equals function    *
     * with a null string pointer >__< - can be called from a static context    *
     ****************************************************************************/
    public static boolean stringMatch(String leftString, String rightString) {

        if ((leftString == null) && (rightString == null))  { return true  ; }
        else if (leftString == null)                        { return false  ; }
        else if (rightString == null)                       { return false  ; }

        return (leftString.equals(rightString))  ;
    }


    /****************************************************************************
     * General function that acts as an alias for String.match() and compares   *
     * the string to a regular expression in order to avoid potentially calling *
     * the matches function with a null string pointer >__<                     *
     ****************************************************************************/
    public static boolean regExpMatch(String inputString, String regularExp) {

        if (inputString == null)    { return false ; }
        if (regularExp == null)     { return false ; }

        return (inputString.matches(regularExp)) ;
    }


    /****************************************************************************
     * General function that returns true if a string is empty; a string is     *
     * considered blank if it is null or contains nothing but whitespace        *
     ****************************************************************************/
    public static boolean blankString(String inputString) {

        if (inputString == null)            { return true ; }
        if (inputString.equals(""))         { return true ; }
        if (inputString.trim().equals(""))  { return true ; }

        return false ;
    }


    /****************************************************************************
	 * Helper function that increments a single character lexoconically        *
	 ****************************************************************************/
	public static String incrementStringDigit(char ch) {

		if (ch == '0')		{ return "1" ; }
		else if (ch == '1')	{ return "2" ; }
		else if (ch == '2')	{ return "3" ; }
		else if (ch == '3')	{ return "4" ; }
		else if (ch == '4')	{ return "5" ; }
		else if (ch == '5')	{ return "6" ; }
		else if (ch == '6')	{ return "7" ; }
		else if (ch == '7')	{ return "8" ; }
		else if (ch == '8')	{ return "9" ; }
		else if (ch == '9')	{ return "A" ; }
		else if (ch == 'A')	{ return "B" ; }
		else if (ch == 'B')	{ return "C" ; }
		else if (ch == 'C')	{ return "D" ; }
		else if (ch == 'D')	{ return "E" ; }
		else if (ch == 'E')	{ return "F" ; }
		else if (ch == 'F')	{ return "G" ; }
		else if (ch == 'G')	{ return "H" ; }
		else if (ch == 'H')	{ return "I" ; }
		else if (ch == 'I')	{ return "J" ; }
		else if (ch == 'J')	{ return "K" ; }
		else if (ch == 'K')	{ return "L" ; }
		else if (ch == 'L')	{ return "M" ; }
		else if (ch == 'M')	{ return "N" ; }
		else if (ch == 'N')	{ return "O" ; }
		else if (ch == 'O')	{ return "P" ; }
		else if (ch == 'P')	{ return "Q" ; }
		else if (ch == 'Q')	{ return "R" ; }
		else if (ch == 'R')	{ return "S" ; }
		else if (ch == 'S')	{ return "T" ; }
		else if (ch == 'T')	{ return "U" ; }
		else if (ch == 'U')	{ return "V" ; }
		else if (ch == 'V')	{ return "W" ; }
		else if (ch == 'W')	{ return "X" ; }
		else if (ch == 'X')	{ return "Y" ; }
		else if (ch == 'Y')	{ return "Z" ; }
		else				{ return null ; }

	}


    /****************************************************************************
     * General function that checks for occurances of the second string in the  *
     * first string and returns true if it is found                             *
     ****************************************************************************/
    public static boolean contains(String leftString, String rightString) {

        if ((leftString == null) && (rightString == null))  { return false  ; }
        else if (leftString == null)                        { return false  ; }
        else if (rightString == null)                       { return false  ; }

        return (leftString.indexOf(rightString) != -1)  ;
    }


    /****************************************************************************
     * General function that runs through the characters of a string to see if  *
     * the string can be parsed as a double                                     *
     ****************************************************************************/
    public static boolean isFloat(String stringObj) {

        if (stringObj == null)              { return false  ; }
        else if (stringObj.length() == 0)   { return false  ; }

        int     stop        = stringObj.length() ;
        char    currChar    = 'a' ;
        boolean allValid    = true ;

        for (int index=0; ((index < stop) && allValid); index++) {

            currChar = stringObj.charAt(index) ;

            if (currChar == '.')        { allValid = true ; }
            else if (currChar == '-')   { allValid = true ; }
            else if (currChar > '9')    { allValid = false ; }
            else if (currChar < '0')    { allValid = false ; }
        }

        return allValid ;
    }


    /****************************************************************************
     * General function that runs through the characters of a string to see if  *
     * the string can be parsed as an integer                                   *
     ****************************************************************************/
    public static boolean isInt(String stringObj) {

        if (stringObj == null)              { return false  ; }
        else if (stringObj.length() == 0)   { return false  ; }

        int     stop        = stringObj.length() ;
        char    currChar    = 'a' ;
        boolean allValid    = true ;

        for (int index=0; ((index < stop) && allValid); index++) {

            currChar = stringObj.charAt(index) ;

            if (currChar == '-')        { allValid = true ; }
            else if (currChar > '9')    { allValid = false ; }
            else if (currChar < '0')    { allValid = false ; }
        }

        return allValid ;
    }


    /****************************************************************************
     * General function that converts a comma seperated list into a Java List   *
     * of Strings                                                               *
     ****************************************************************************/
    public static List<String> buildListFromCSV(String csvList) {

        if (csvList == null)            { return (new ArrayList<String>()) ; }
        if (csvList.trim().equals(""))  { return (new ArrayList<String>()) ; }

        List<String> stringList  = (new ArrayList<String>()) ;
        String[]     tokens      = csvList.split(",") ;
        String       currToken   = null ;
        int          numTokens   = tokens.length ;

        for (int index=0; index<numTokens; index++) {

            currToken = tokens[index] ;

            // Don't bother adding emtpy elements
            if (currToken.trim().equals(""))    { continue ; }
            else                                { stringList.add(currToken.trim()) ; }
        }

        return stringList ;
    }


    /****************************************************************************
     * General function that converts a String array to a Java List of Strings  *
     ****************************************************************************/
    public static List<String> buildList(String[] stringArray) {

        if (stringArray == null)        { return (new ArrayList<String>()) ; }
        if (stringArray.length == 0)    { return (new ArrayList<String>()) ; }

        List<String> stringList  = (new ArrayList<String>()) ;
        String       currString  = null ;
        int          listLength  = stringArray.length ;

        for (int index=0; index<listLength; index++) {

            currString = stringArray[index] ;

            // Don't bother adding emtpy elements
            if (blankString(currString))    { continue ; }
            else                            { stringList.add(currString.trim()) ; }
        }

        return stringList ;
    }


    /****************************************************************************
     * Takes a source string and espaces the requested characters               *
     ****************************************************************************/
    public static String escapeChars(String srcString, String charSet) {

        if (emptyString(srcString)) { return srcString ; }
        if (emptyString(charSet))   { return srcString ; }


        StringCharacterIterator letterHopper    = new StringCharacterIterator(charSet) ;
        String                  result          = srcString ;
        char                    currChar        = letterHopper.first() ;
        boolean                 gotBackslash    = false ;

        while (currChar != CharacterIterator.DONE) {

            if (currChar == '\\' && !gotBackslash)  { result = escape(result, currChar, '\\') ; }
            else                                    { result = escape(result, currChar, '\\') ; }

            gotBackslash    = (gotBackslash || (currChar == '\\')) ;
            currChar        = letterHopper.next() ;
        }

        return result;
    }


    /****************************************************************************
     * Support function for escapeChars                                         *
     ****************************************************************************/
    public static String escape(String haystack, char needle, char escapeChar) {

        if (emptyString(haystack)) { return haystack ; }


        StringCharacterIterator letterHopper    = new StringCharacterIterator(haystack) ;
        String                  escapedString   = "" ;
        char                    currChar        = letterHopper.first() ;

        while (currChar != CharacterIterator.DONE) {

            if (currChar == needle) { escapedString += String.valueOf(escapeChar) ; }

            escapedString   = escapedString+String.valueOf(currChar) ;
            currChar        = letterHopper.next() ;
        }

        return escapedString ;
    }

    
    /****************************************************************************
     * Support function for escapeChars                                         *
     ****************************************************************************/
    public static int strlen(String target) {
        
        int theLength = 0 ;
        
        if (emptyString(target))    { return theLength ; }
        else                        { theLength = target.length() ; }
        
        return theLength ;
    }
}
