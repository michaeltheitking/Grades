package cs315.prog;

/**
 * This class reads in a set of grades from a file and stores it for each student. <BR>
 * Also computes various stats on the grades, like overall GPA, standard deviation, etc.
 * @author michael king and michael fradkin
 * @version 2.98.423.1
 */
public class Grades implements java.lang.Cloneable
{

    /** holds data for every student in the file */
    private Student[] myStudents;
    /** captures number of students in the file */
    private int    iMyNumRecords;
    /** captures all the numbers of quizzes, tests, and projects into a single string */
    private String allMyNumbers;
    /** captures just number of quizzes */
    private int    iMyNumQuizzes;
    /** captures just number of projects */
    private int    iMyNumProjects;
    /** captures just number of tests */
    private int    iMyNumTests;

    /** captures all of the values of quizzes, tests, and projects into a single string */
    private String allMyValues;
    /** captures all quiz values into an array */
    private int[]  myMaxQuizValues;
    /** captures all project values into an array */
    private int[]  myMaxProjValues;
    /** captures all test values into an array */
    private int[]  myMaxTestValues;
    /** captures all the weights of quizzes, tests, and projects into a single string */
    private String allMyWeights;
    /** captures all quiz weights into an array */
    private int[]  myQuizWeights;
    /** captures all project weights into an array */
    private int[]  myProjWeights;
    /** captures all test weights into an array */
    private int[]  myTestWeights;

    /** captures minimum score for all grades */
    private double dMyMinScore;
    /** captures maximum score for all grades */
    private double dMyMaxScore;
    /** captures average score for all grades */
    private double dMyMeanScore;
    /** captures standard deviation of all grades */
    private double dMyStandardDev;
    /** captures percentage of grades within 1 standard deviation */
    private double dMyStandardDev1;
    /** captures percentage of grades within 2 standard deviations */
    private double dMyStandardDev2;
    /** captures percentage of As out of all grades */
    private double dMyPercentAs;
    /** captures percentage of Bs out of all grades */
    private double dMyPercentBs;
    /** captures percentage of Cs out of all grades */
    private double dMyPercentCs;
    /** captures percentage of Ds out of all grades */
    private double dMyPercentDs;
    /** captures percentage of Fs out of all grades */
    private double dMyPercentFs;
    /** captures overall GPA out of all the grades */
    private double dMyClassGPA;

    /** allows tokenizing of lines in the file */
    java.util.StringTokenizer st;

    /**
     * Constructor for Grades <BR>
     * pre: a file has already been opened for reading. <BR>
     * post: grades for every student are imported from a file and stored. <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(N) <BR>
     * @param file is the object created from the input file
     * @exception BadInputException if input is not valid.
     */
    public Grades (ReadFile file) throws BadInputException {

        //number of students is first line in the file
        //Have to tokenize it because it has a semicolon!
        //Have to convert all of these to numbers...could throw exceptions
        try
        {
            st = new java.util.StringTokenizer(file.readLine(), " ;\t\n\r\f");
            iMyNumRecords = Integer.parseInt( st.nextToken() );

            allMyNumbers = file.readLine();
            st = new java.util.StringTokenizer(allMyNumbers, " ;\t\n\r\f");

            //the number of tokens in this line should be the number of categories: projects, quizzes, tests
            assert (st.countTokens() == 3);

            iMyNumQuizzes = Integer.parseInt( st.nextToken() );

            iMyNumProjects = Integer.parseInt( st.nextToken() );

            iMyNumTests = Integer.parseInt( st.nextToken() );
        }
        catch (NumberFormatException e) {
            throw new BadInputException();
        }


        //initiliaze the grades of each category in an array
        myMaxQuizValues = new int[iMyNumQuizzes];
        myMaxProjValues = new int[iMyNumProjects];
        myMaxTestValues = new int[iMyNumTests];

        //initialize the weights (worth) of each category in an array
        myQuizWeights = new int[iMyNumQuizzes];
        myProjWeights = new int[iMyNumProjects];
        myTestWeights = new int[iMyNumTests];

        //there should be as many tokens on this line of grades as total num of all categories
        allMyValues = file.readLine();
        st = new java.util.StringTokenizer(allMyValues, " ;\t\n\r\f");
        assert(st.countTokens() == (iMyNumQuizzes + iMyNumProjects + iMyNumTests) );

        //there should be as many tokens on this line of weights as total num of all categories
        allMyWeights = file.readLine();
        java.util.StringTokenizer st2 = new java.util.StringTokenizer(allMyWeights, " ;\t\n\r\f");
        assert(st2.countTokens() == (iMyNumQuizzes + iMyNumProjects + iMyNumTests) );

        //store all quiz grades and weights in their corresponding arrays
        for (int x = 0; x < iMyNumQuizzes; x++)
        {
            try {
                myMaxQuizValues[x] = Integer.parseInt( st.nextToken() );
                myQuizWeights[x] = Integer.parseInt( st2.nextToken() );
            }
            catch (NumberFormatException e) {
                throw new BadInputException();
            }
        }

        //store all project grades and weights in their corresponding arrays
        for (int y = 0; y < iMyNumProjects; y++) {
            try {
                myMaxProjValues[y] = Integer.parseInt( st.nextToken() );
                myProjWeights[y] = Integer.parseInt( st2.nextToken() );
            }
            catch (NumberFormatException e) {
                throw new BadInputException();
            }
        }

        //store all test grades and weights in their corresponding arrays
        for(int z = 0; z < iMyNumTests; z++) {
            try {
                myMaxTestValues[z] = Integer.parseInt( st.nextToken() );
                myTestWeights[z] = Integer.parseInt( st2.nextToken() );
            }
            catch (NumberFormatException e) {
                throw new BadInputException();
            }
        }

        //initialize the students, stored in an array
        myStudents = new Student[iMyNumRecords];

        for(int x = 0; x < iMyNumRecords; x++)
            myStudents[x] = new Student(file);

        if (! this.isValid())
            throw new BadInputException();

        //compute overall statistics
        setPercentLetterGrades();
        setMinMaxMeanScore();
        setStandardDeviations();
        setClassGPA();
    }

    /**
     * Sets the standard deviation for all the grades.
     * pre: none <BR>
     * post: standard deviation, percentage of scores within 1 and 2 deviations is computed and stored. <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(N)<BR>
     */
    private void setStandardDeviations()
    {   double sum = 0.0;

        //get the sum of all scores
        for(int x = 0; x < iMyNumRecords; x++)
           sum += Math.pow(myStudents[x].dMyTotalScore,2);

        sum = sum / (double) iMyNumRecords;
        dMyStandardDev = Math.sqrt(sum - Math.pow(dMyMeanScore,2));

        //compute number of scores within 1 standard deviation
        for(int y = 0; y < iMyNumRecords; y++)
        {   if(myStudents[y].dMyTotalScore <= dMyMeanScore + dMyStandardDev &&
               myStudents[y].dMyTotalScore >= dMyMeanScore - dMyStandardDev)
            {   dMyStandardDev1++; }
        }

        dMyStandardDev1 = dMyStandardDev1 / (double) iMyNumRecords * 100;

        //compute number of scores within 2 standard deviations
        for(int z = 0; z < iMyNumRecords; z++)
        {   if(myStudents[z].dMyTotalScore <= dMyMeanScore + dMyStandardDev * 2 &&
               myStudents[z].dMyTotalScore >= dMyMeanScore - dMyStandardDev * 2)
            {   dMyStandardDev2++; }
        }

        dMyStandardDev2 = dMyStandardDev2 / (double) iMyNumRecords * 100;
    }

    /**
     * Computes and stores percentages of As, Bs, Cs, Ds, and Fs. <BR>
     * pre: letter grades for each student have already been computed. <BR>
     * post: percentages of each grade are calculated and stored. <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(1) <BR>
     */
    private void setPercentLetterGrades()
    {
        //find and tally amount of each letter grade
        for(int x = 0; x < iMyNumRecords; x++)
        {
            if(myStudents[x].cMyLetterGrade == 'A')
                ++dMyPercentAs;

            else if(myStudents[x].cMyLetterGrade == 'B')
                ++dMyPercentBs;

            else if(myStudents[x].cMyLetterGrade == 'C')
                ++dMyPercentCs;

            else if(myStudents[x].cMyLetterGrade == 'D')
                ++dMyPercentDs;

            else
                ++dMyPercentFs;
        }

        //percentage is the amount found divided by the whole, multiplied by 100
        dMyPercentAs = dMyPercentAs / (double) iMyNumRecords * 100;
        dMyPercentBs = dMyPercentBs / (double) iMyNumRecords * 100;
        dMyPercentCs = dMyPercentCs / (double) iMyNumRecords * 100;
        dMyPercentDs = dMyPercentDs / (double) iMyNumRecords * 100;
        dMyPercentFs = dMyPercentFs / (double) iMyNumRecords * 100;

    }

    /**
     * Computes and stores the minimum, maximum, and average score. <BR>
     * pre: grades have already been computed. <BR>
     * post: minimum, maximum value is found and stored. average score is computed. <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(1) <BR>
     */
    private void setMinMaxMeanScore()
    {
        //find and set minimum score
        dMyMinScore = myStudents[0].dMyTotalScore;
        for(int x = 0; x < iMyNumRecords; x++)
        {
            if(myStudents[x].dMyTotalScore < dMyMinScore)
                dMyMinScore = myStudents[x].dMyTotalScore;
        }

        //find and set maximum score
        dMyMaxScore = myStudents[0].dMyTotalScore;
        for(int x = 0; x < iMyNumRecords; x++)
        {
            if(myStudents[x].dMyTotalScore > dMyMaxScore)
                dMyMaxScore = myStudents[x].dMyTotalScore;
        }

        //find and set average score
        for(int x = 0; x < iMyNumRecords; x++)
            dMyMeanScore += myStudents[x].dMyTotalScore;

        dMyMeanScore = (dMyMeanScore / (double) iMyNumRecords);
    }

    /**
     * Computes and stores the overall class GPA <BR>
     * pre: letter grades for each student have already been computed. <BR>
     * post: class GPA is calculated and stored. <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(1) <BR>
     */
    private void setClassGPA()
    {
        for(int x = 0; x < iMyNumRecords; x++)
        {   if(myStudents[x].cMyLetterGrade == 'A')
                dMyClassGPA += 4.0;

            if(myStudents[x].cMyLetterGrade == 'B')
                dMyClassGPA += 3.0;

            if(myStudents[x].cMyLetterGrade == 'C')
                dMyClassGPA += 2.0;

            if(myStudents[x].cMyLetterGrade == 'D')
                dMyClassGPA += 1.0;

            else
                dMyClassGPA += 0.0;
        }

        dMyClassGPA = dMyClassGPA / (double) iMyNumRecords;

    }

    /**
     * Determines the invariant of the Grades class. <BR>
     * pre: none. <BR>
     * post: Returns true if all checked data is valid. <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(1) <BR>
     */
    public boolean isValid()
    {
        int weightCounter = 0;			//counter to add up weights
/*
		//Scores and percentages should be 0 to 100
        if ((dMyMinScore < 0) || (dMyMinScore > 100))
            return false;
        if ((dMyMaxScore < 0) || (dMyMaxScore > 100))
            return false;
        if ((dMyMeanScore < 0) || (dMyMeanScore > 100))
            return false;
        if ((dMyStandardDev < 0) || (dMyStandardDev > 100))
            return false;
        if ((dMyStandardDev1 < 0) || (dMyStandardDev1 > 100))
            return false;
        if ((dMyStandardDev2 < 0) || (dMyStandardDev2 > 100))
            return false;
        if ((dMyPercentAs < 0) || (dMyPercentAs > 100))
            return false;
        if ((dMyPercentBs < 0) || (dMyPercentBs > 100))
            return false;
        if ((dMyPercentCs < 0) || (dMyPercentCs > 100))
            return false;
        if ((dMyPercentDs < 0) || (dMyPercentDs > 100))
            return false;
        if ((dMyPercentFs < 0) || (dMyPercentFs > 100))
            return false;
        if ((dMyClassGPA < 0) || (dMyClassGPA > 4))
            return false;

		//This data must be greater than zero or there is nothing to computer
        if (iMyNumRecords < 0)
            return false;
        if (iMyNumQuizzes < 0)
            return false;
        if (iMyNumProjects < 0)
            return false;
        if (iMyNumTests < 0)
            return false;

		//Weights and values must be greater than zero
        for (int i=0; i < myMaxQuizValues.length; i++)
            if ( myMaxQuizValues[i] <= 0 )
                return false;

        for (int i=0; i < myMaxProjValues.length; i++)
            if ( myMaxProjValues[i] <= 0 )
                return false;

        for (int i=0; i < myMaxTestValues.length; i++)
            if ( myMaxTestValues[i] <= 0 )
                return false;

        for (int i=0; i < myQuizWeights.length; i++) {
            if ( myQuizWeights[i] <= 0 )
                return false;
            else
                weightCounter += myQuizWeights[i];			//Count up the weights
        }

        for (int i=0; i < myProjWeights.length; i++) {
            if ( myProjWeights[i] <= 0 )
                return false;
            else
                weightCounter += myProjWeights[i];
        }

        for (int i=0; i < myTestWeights.length; i++) {
            if ( myTestWeights[i] <= 0 )
                return false;
            else
                weightCounter += myTestWeights[i];
        }

        if (weightCounter != 100)					//Weights should equal 100
            return false;
*/
        return true;				//If to this point, all data is valid
    }

    /**
     * Creates a deep copy of a Grades object. <BR>
     * pre: object implements Cloneable <BR>
     * post: deep copy of Grades object is created and returned <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(N) <BR>
     * @return deep copy of Grades object as Object
     */
    public Object clone()
    {
        try
        {
            Grades g = (Grades)super.clone();

            //individually clone each student for deep copies
            for(int x = 0; x <= iMyNumRecords; x++)
                g.myStudents[x] = (Student)myStudents[x].clone();

            return g;
        }
        catch(CloneNotSupportedException e)
        {   return null;    }
    }


    /**
     * Method to print out sorted by letter grade, total score, then ID. <BR>
     * post: output is placed into the PrintWriter destination. <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(1) <BR>
     * @param out java.io.PrintWriter object specifies where to output the text
     * @return the modified PrintWriter object
     */
    public java.io.PrintWriter writeByLetter(java.io.PrintWriter out)
    {
        java.text.DecimalFormat num = new java.text.DecimalFormat( "0.00" );

        //print stats for overall grades
        out.println("Size  = " + iMyNumRecords                           + '\n');
        out.println("Min   = " + append(num.format(dMyMinScore), 7)      + "%");
        out.println("Max   = " + append(num.format(dMyMaxScore), 7)      + "%" + '\n');
        out.println("Mean  = " + append(num.format(dMyMeanScore), 7)     + "%");
        out.println("SDev  = " + append(num.format(dMyStandardDev), 7)   + "%" + '\n');
        out.println("SDev1 = " + append(num.format(dMyStandardDev1), 7)  + "%");
        out.println("SDev2 = " + append(num.format(dMyStandardDev2), 7)  + "%" + '\n');
        out.println("As    = " + append(num.format(dMyPercentAs), 7)     + "%");
        out.println("Bs    = " + append(num.format(dMyPercentBs), 7)     + "%");
        out.println("Cs    = " + append(num.format(dMyPercentCs), 7)     + "%");
        out.println("Ds    = " + append(num.format(dMyPercentDs), 7)     + "%");
        out.println("Fs    = " + append(num.format(dMyPercentFs), 7)     + "%" + '\n');
        out.println("GPA   = " + append(num.format(dMyClassGPA), 7)      + '\n');
        out.println("Numbers");
        out.println("-------");
        out.println(allMyNumbers + '\n');
        out.println("Values");
        out.println("------");
        out.println(allMyValues + '\n');
        out.println("Weights");
        out.println("-------");
        out.println(allMyWeights + '\n');
        printHeader(out);
        java.util.Arrays.sort(myStudents, new LetterGradeComparator());

        //print out each student
        for(int x = 0; x < iMyNumRecords; x++)
        {   //break up by letter grade
            if(x > 0 && myStudents[x].cMyLetterGrade != myStudents[x-1].cMyLetterGrade)
            {   out.println("..." + '\n');
                printHeader(out);
            }
            myStudents[x].write(out);
        }
        out.println("..." + '\n');
        return out;
    }

    /**
     * Method to print out sorted by section number, then ID. <BR>
     * post: output is placed into the PrintWriter destination. <BR>
     * complexity: Big O(N) <BR>
     * memory usage: Big O(1) <BR>
     * @param out java.io.PrintWriter object specifies where to output the text
     * @return the modified PrintWriter object
     */
    public java.io.PrintWriter writeBySection(java.io.PrintWriter out)
    {
        java.text.DecimalFormat num = new java.text.DecimalFormat( "0.00" );

        //print stats for overall grades
        out.println("Size  = " + iMyNumRecords                           + '\n');
        out.println("Min   = " + append(num.format(dMyMinScore), 7)      + "%");
        out.println("Max   = " + append(num.format(dMyMaxScore), 7)      + "%" + '\n');
        out.println("Mean  = " + append(num.format(dMyMeanScore), 7)     + "%");
        out.println("SDev  = " + append(num.format(dMyStandardDev), 7)   + "%" + '\n');
        out.println("SDev1 = " + append(num.format(dMyStandardDev1), 7)  + "%");
        out.println("SDev2 = " + append(num.format(dMyStandardDev2), 7)  + "%" + '\n');
        out.println("As    = " + append(num.format(dMyPercentAs), 7)     + "%");
        out.println("Bs    = " + append(num.format(dMyPercentBs), 7)     + "%");
        out.println("Cs    = " + append(num.format(dMyPercentCs), 7)     + "%");
        out.println("Ds    = " + append(num.format(dMyPercentDs), 7)     + "%");
        out.println("Fs    = " + append(num.format(dMyPercentFs), 7)     + "%" + '\n');
        out.println("GPA   = " + append(num.format(dMyClassGPA), 7)      + '\n');
        out.println("Numbers");
        out.println("-------");
        out.println(allMyNumbers + '\n');
        out.println("Values");
        out.println("------");
        out.println(allMyValues + '\n');
        out.println("Weights");
        out.println("-------");
        out.println(allMyWeights + '\n');
        printHeader(out);
        java.util.Arrays.sort(myStudents, new SectionComparator());

        //print out each student
        for(int x = 0; x < iMyNumRecords; x++)
        {   //break up section numbers
            if(x > 0 && myStudents[x].iMySectionNum != myStudents[x-1].iMySectionNum)
            {   out.println("..." + '\n');
                printHeader(out);
            }
            myStudents[x].write(out);
        }
        out.println("..." + '\n');
        return out;
    }

    /**
     * Method to print out the header for subsections of students. <BR>
     * post: header is printed out <BR>
     * complexity: Big O(1) <BR>
     * memory usage: Big O(1) <BR>
     * @param out java.io.PrintWriter object specifies where to output the text
     * @return the modified PrintWriter object
     */
    private java.io.PrintWriter printHeader(java.io.PrintWriter out)
    {
        out.println("ID      Sec     TA         Quizzes                         Projects         Tests         Extra   Total1    Total2    Total3    G");
        out.println("--      ---     --         -------                         --------         -----         -----   ------    ------    ------    -");
        return out;
    }

    /**
     * Private method to format the number for correct spacing in the table. <BR>
     * pre: numToFormat instanceOf String <BR>
     * post: the parameter is returned with an appended number of spaces in front of it <BR>
     * Big O: O(N) <BR>
     * Memory estimate: Big O(N) <BR>
     * @param numToFormat the string representation of the number to format
     * @param formatTo the amount of space necessary before the number to align correctly
     * @return the newly appended string
     */
    private String append(String numToFormat, int formatTo)
    {

        while(numToFormat.length() < formatTo)
            numToFormat = " " + numToFormat;

         return numToFormat;
    }//end method

    /**
     * Private class implementing Comparator interface allows for  <BR>
     * sorting by letter grade, total score, then ID. <BR>
     * memory usage: Big O(1) <BR>
     * @author michael king and michael fradkin
     * @version 2.0
     */
    private class LetterGradeComparator implements java.util.Comparator
    {
        /**
         * Method to compare based on letter grade, total score, then ID. <BR>
         * pre: Objects are instanceof Student <BR>
         * post: If sorted using this comparator, the array will be modified accordingly. <BR>
         * memory usage: Big O(1) <BR>
         * @param s1 Student object
         * @param s2 Student object
         * @return the number if one is less than the other, 0 if equal, otherwise a positive number
         */
        public int compare(Object s1, Object s2)
        {
            //convert to Student objects
            Student stud1 = (Student)s1;
            Student stud2 = (Student)s2;
            //wrap data for use of compareTo
            Character letterCheck = new Character(stud1.cMyLetterGrade);
            Character letterCheck2 = new Character(stud2.cMyLetterGrade);
            Double scoreCheck = new Double(stud1.dMyTotalScore);
            Double scoreCheck2 = new Double(stud2.dMyTotalScore);

            //first sort by letter grade
            if(letterCheck.compareTo(letterCheck2) == 0)
            {   //if ==, sort by total score
                if(scoreCheck2.compareTo(scoreCheck) == 0)
                {       //if ==, sort by id number
                        return stud1.myID.compareTo(stud2.myID);
                }
                else
                    return scoreCheck2.compareTo(scoreCheck);
            }
            else
                return letterCheck.compareTo(letterCheck2);
        }
    }//end inner class

    /**
     * Private class implementing Comparator interface allows for  <BR>
     * sorting by section number, then ID. <BR>
     * memory usage: Big O(1) <BR>
     * @author michael king and michael fradkin
     * @version 2.0
     */
    private class SectionComparator implements java.util.Comparator
    {
        /**
         * Method to compare based on section number, then ID <BR>
         * pre: Objects are instanceof Student <BR>
         * post: If sorted using this comparator, the array will be modified accordingly. <BR>
         * memory usage: Big O(1) <BR>
         * @param s1 Student object
         * @param s2 Student object
         * @return the number if one is less than the other, 0 if equal, otherwise a positive number
         */
        public int compare(Object s1, Object s2)
        {
            Student stud1 = (Student)s1;
            Student stud2 = (Student)s2;
            Integer temp = new Integer(stud1.iMySectionNum);
            Integer temp2 = new Integer(stud2.iMySectionNum);
            if(temp.compareTo(temp2) == 0)
                return stud1.myID.compareTo(stud2.myID);
            else
                return temp.compareTo(temp2);

        }
    }//end inner class

    /**
     * This inner class contains all properties of a student, and computes various grades.  <BR>
     * @author michael king and michael fradkin
     * @version 2.1.382.1
     */
    public class Student implements java.lang.Cloneable {

        /** captures student ID */
        private String  myID;
        /** captures student section number */
        private int     iMySectionNum;
        /** captures student's TA's name */
        private String  myTA;

        /** captures all quiz grades for a student */
        private int[]   myQuizzes;
        /** captures all project grades for a student */
        private int[]   myProjects;
        /** captures all test grades for a student */
        private int[]   myTests;
        /** captures value of a student's extra credit */
        private double  dMyExtraCredit;

        /** captures the letter grade of a student */
        private char    cMyLetterGrade;
        /** stores if the student did extra credit (+) or was demoted (-) */
        private char    cMyPlusMinus;
        /** captures average project grade, weighted */
        private double  dMyTotalProjScore;
        /** captures weighted average combination of quiz and test grades */
        private double  dMyTotalQuizTestScore;
        /** captures a student's overall average */
        private double  dMyTotalScore;

        /** allows for breaking up lines in a file */
        java.util.StringTokenizer lineTokens;

        /**
         * Constructor of Students <BR>
         * pre: grades have been read in. <BR>
         * post: Student is initialized based on a file's grades. <BR>
         * complexity: Big O(N) <BR>
         * memory usage: Big O(1) <BR>
         * @param file a valid, opened file.
         * @exception BadInputException if input in the file is not valid.
         **/
        public Student (ReadFile file) throws BadInputException
        {
            lineTokens = new java.util.StringTokenizer(file.readLine(),  " ;\t\n\r\f");

            //the line should have as many tokens as grades did, and four more (id, section, TA, and extra credit)
            assert ( lineTokens.countTokens() == (iMyNumQuizzes + iMyNumProjects + iMyNumTests + 4) );
            myID = lineTokens.nextToken();

            try
            {
                iMySectionNum = Integer.parseInt( lineTokens.nextToken() );

                myTA = lineTokens.nextToken();

                //Create array and get the data
                myQuizzes = new int[iMyNumQuizzes];
                for(int x = 0; x < iMyNumQuizzes; x++)
                   myQuizzes[x] = Integer.parseInt( lineTokens.nextToken() );

                myProjects = new int[iMyNumProjects];
                for(int y = 0; y < iMyNumProjects; y++)
                    myProjects[y] = Integer.parseInt( lineTokens.nextToken() );

                myTests = new int[iMyNumTests];
                for(int z = 0; z < iMyNumTests; z++)
                    myTests[z] = Integer.parseInt( lineTokens.nextToken() );
            }

            catch (NumberFormatException e) {

                throw new BadInputException();
            }


            //last token should be extra credit
            dMyExtraCredit = Double.parseDouble( lineTokens.nextToken() );

            //Compute grade totals
            setTotalProjectScore();
            setTotalQuizTestScore();
            setTotalScore();
            setLetterGrade();

            //make sure all the data is valid
            if (! this.isValid() )

                throw new BadInputException();
        }

        /**
         * Computes letter grade for Student. <BR>
         * post: letter grade is determined, based on in-class and out-class components. <BR>
         * complexity: Big O(1)<BR>
         * memory usage: Big O(1)<BR>
         **/
        private void setLetterGrade()
        {   //automatically fail, no matter what
            if (dMyTotalScore < 60) {

                cMyLetterGrade = 'F';
                //still may have earned extra credit
                if (dMyExtraCredit > 0.0)
                    cMyPlusMinus = '+';
                else
                    cMyPlusMinus = ' ';
            }
            else if (dMyTotalScore < 70) {
                //check to see if they fail one component
                if ((dMyTotalProjScore < 60.0) || (dMyTotalQuizTestScore < 60.0)) {
                    cMyPlusMinus = '-';
                    cMyLetterGrade = 'F';
                }
                else {
                    cMyLetterGrade = 'D';

                    if (dMyExtraCredit > 0.0)
                        cMyPlusMinus = '+';
                    else
                        cMyPlusMinus = ' ';
                }
            }
            else {
                //check to see if they fail one component
                if ((dMyTotalProjScore < 60.0) || (dMyTotalQuizTestScore < 60.0)) {
                    cMyLetterGrade = 'F';
                    cMyPlusMinus = '-';
                }
                //check to see if they didnt get at least a c in both components
                else if ((dMyTotalProjScore < 70.0) || (dMyTotalQuizTestScore < 70.0)) {
                    cMyLetterGrade = 'D';
                    cMyPlusMinus = '-';
                }
                //compute normally
                else {
                    if (dMyTotalScore >= 90.0)
                        cMyLetterGrade = 'A';
                    else if (dMyTotalScore >= 80.0)
                        cMyLetterGrade = 'B';
                    else
                        cMyLetterGrade = 'C';

                    if (dMyExtraCredit > 0.0)
                        cMyPlusMinus = '+';
                    else
                        cMyPlusMinus = ' ';
                }
            }
        }

        /**
         * Computes and stores total project score, weighted. <BR>
         * post: project grade is computed on a 100 point scale, accounting for weights. <BR>
         * complexity: Big O(N) <BR>
         * memory usage: Big O(1) <BR>
         **/
        private void setTotalProjectScore()
        {
            int totalPoss = 0;


            for(int x = 0; x < iMyNumProjects; x++)
            {   dMyTotalProjScore += ((double)(myProjects[x] / (double) myMaxProjValues[x]) * myProjWeights[x]);
                totalPoss += myProjWeights[x];
            }
            dMyTotalProjScore = dMyTotalProjScore / (double)totalPoss * 100;
            dMyTotalProjScore = (double)(Math.round(dMyTotalProjScore*100)/ (double)100);
        }

        /**
         * Computes and stores combined quiz and test score, weighted. <BR>
         * post: combined quiz and test scores are computed on a 100 point scale, accounting for weights. <BR>
         * complexity: Big O(N) <BR>
         * memory usage: Big O(1) <BR>
         **/
        private void setTotalQuizTestScore()
        {
            int totalPossQuiz = 0;
            int totalPossTest = 0;

            //average quizzes
            for(int x = 0; x < iMyNumQuizzes; x++)
            {   dMyTotalQuizTestScore += ((double)myQuizzes[x] / (double) myMaxQuizValues[x]) * myQuizWeights[x];
                totalPossQuiz += myQuizWeights[x];
            }
            //add the average tests
            for(int y = 0; y < iMyNumTests; y++)
            {   dMyTotalQuizTestScore += (double)(myTests[y] / (double) myMaxTestValues[y]) * myTestWeights[y];
                totalPossTest += myTestWeights[y];
            }

            dMyTotalQuizTestScore = dMyTotalQuizTestScore / (double) (totalPossQuiz + totalPossTest) * 100;
            dMyTotalQuizTestScore = (double)(Math.round(dMyTotalQuizTestScore * 100) / (double)100);
        }

        /**
         * Computes and stores the overall grade for the student. <BR>
         * post: overall grade is computed and stored, accounting for extra credit. <BR>
         * complexity: Big O(1) <BR>
         * memory usage: Big O(1) <BR>
         **/
        private void setTotalScore()
        {
            dMyTotalScore = (double)(Math.round((dMyTotalProjScore * .4 + dMyTotalQuizTestScore *.6) * 100) / (double)100);
            dMyTotalScore += dMyExtraCredit;
        }

        /**
         * Determines the invariant of the Student class. <BR>
         * post: returns the truth value of valid data. <BR>
         * complexity: Big O(N) <BR>
         * memory usage: Big O(1) <BR>
         * @return true if data is valid; false otherwise
         **/
        private boolean isValid() {

            if ((myID == null) || (myID.length() != 5))
                return false;

            if ((iMySectionNum <= 9999) || (iMySectionNum >= 100000))
                return false;

            if ((myTA == null) || (myTA.length() > 8))
                return false;

            for (int i=0; i < myQuizzes.length; i++) {
                if ((myQuizzes[i] < 0) || (myQuizzes[i] > 99))
                    return false;
                if (myQuizzes[i] > myMaxQuizValues[i])
                    return false;
            }

            for (int i=0; i < myProjects.length; i++) {
                if ((myProjects[i] < 0) || (myProjects[i] > 99))
                    return false;
                if (myProjects[i] > myMaxProjValues[i])
                    return false;
            }

            for (int i=0; i < myTests.length; i++) {
                if ((myTests[i] < 0) || (myTests[i] > 999))
                    return false;
                if (myTests[i] > myMaxTestValues[i])
                    return false;
            }

            if (dMyTotalProjScore < 0)
                return false;

            if (dMyTotalQuizTestScore < 0)
                return false;

            if (dMyTotalScore < 0)
                return false;

            if (dMyExtraCredit < 0)
                return false;

            return true;
        }

       /**
        * Method to print out each students info <BR>
        * post: output is placed into the PrintWriter destination. <BR>
        * complexity: Big O(N) <BR>
        * memory usage: Big O(1) <BR>
        * @param out java.io.PrintWriter object specifies where to output the text
        * @return the modified PrintWriter object
        */
        public java.io.PrintWriter write(java.io.PrintWriter out)
        {
            String allMyQuizzes = "";
            String allMyProjects = "";
            String allMyTests = "";
            for(int x = 0; x < iMyNumQuizzes; x++)
                    allMyQuizzes += append( myQuizzes[x] + "", 2, false ) + " ";

            for(int y = 0; y < iMyNumProjects; y++)
                    allMyProjects += append( myProjects[y] + "", 2, false ) + " ";

            for(int z = 0; z < iMyNumTests; z++)
                    allMyTests += append( myTests[z] + "", 3, false ) + " ";

            myTA = append(myTA, 8, true);

            java.text.DecimalFormat n = new java.text.DecimalFormat( "0.00" );
            out.print(myID + " ; ");
            out.print(iMySectionNum + " ; ");
            out.print(myTA + " ; ");
            out.print(allMyQuizzes + "; ");
            out.print(allMyProjects + "; ");
            out.print(allMyTests + "; ");
            //append each with whitespace for formatting purposes
            out.print(append(n.format(dMyExtraCredit) + "", 5, false) + " ; ");
            out.print(append(n.format(dMyTotalProjScore) + "", 6, false) + "% ; ");
            out.print(append(n.format(dMyTotalQuizTestScore) + "", 6, false) + "% ; ");
            out.print(append(n.format(dMyTotalScore) + "", 6, false) + "% ; " + cMyLetterGrade);

            if (cMyPlusMinus != ' ')
                out.print(cMyPlusMinus + "\n");
            else
                out.print("\n");

            return out;
        }

        /**
         * Private method to format the number for correct spacing in the table. <BR>
         * pre: numToFormat instanceOf String <BR>
         * post: the parameter is returned with an appended number of spaces in front of it <BR>
         * Big O: O(N) <BR>
         * Memory estimate: Big O(N) <BR>
         * @param numToFormat the string representation of the number to format
         * @param formatTo the amount of space necessary before the number to align correctly
         * @return the newly appended string
         */
        private String append(String numToFormat, int formatTo, boolean alignLeft)
        {
            if (alignLeft)
                while(numToFormat.length() < formatTo)
                    numToFormat = numToFormat + " ";
            else
                while(numToFormat.length() < formatTo)
                    numToFormat = " " + numToFormat;

             return numToFormat;
        }//end method


       /**
        * Creates a deep copy of a Student object. <BR>
        * pre: object implements Cloneable <BR>
        * post: deep copy of Student object is created and returned <BR>
        * complexity: Big O(1) <BR>
        * memory usage: Big O(1) <BR>
        * @return deep copy of Student object as Object
        */
        public Object clone()
        {
            try {

                return (Student)super.clone();
            }
            catch(CloneNotSupportedException e) {

                return null;
            }
        }

    }//end inner class Student

}//end outer class Grades