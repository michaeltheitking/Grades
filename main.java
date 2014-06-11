// ------------------
// Grades/main.java
// Copyright (C) 2002
// Michael Fradkin and Michael King
// ------------------

// ----
// main
// ----

/**
 * @author  Michael Fradkin and Michael King
 * @version 1.0
 */

public class main
{
    /**
     * Drives the program.
     * @param args command-line input, of which there is none
     */
    public static void main( String[] args )
    {
        cs315.prog.ReadFile file = null;				//ReadFile reference
        cs315.prog.Grades grd = null;					//Grade reference
        java.io.FileWriter fout = null;		//FileWriter reference
        try
        {
			//Construct FileWriter object with file to write to
            fout = new java.io.FileWriter("main.out");
            //Contruct a PrintWriter to output with (takes FileWriter)
            java.io.PrintWriter out = new java.io.PrintWriter(fout);
            //Contstruct Time object and start timer
            cs315.util.Timer t = new cs315.util.Timer();
            t.start("Grades", out);

            file = new cs315.prog.ReadFile("main.in");			//File to read from
            grd = new cs315.prog.Grades(file);					//New grades object (takes the file with the input)

			//Output the records, sorting first by letter grade, then total score, then ID
            grd.writeByLetter(out);
            //Output the records, sorting first by section number, then ID
            grd.writeBySection(out);
            //Turn off timer
            t.finish();

        }

		//Catches exceptions thrown and prints the errors
        catch(cs315.prog.BadInputException e)
        {
            System.out.println( e );
        }

        catch(java.io.IOException e)
        {
            System.out.println( "The file was not found: " + e );
        }

        //In any case, try to close the file
        finally
        {
            try
            {
				//Makes sure references are null and then close the streams
                if (file != null)
                    file.close();
                if(fout != null)
                    fout.close();
            }
            //Can't close the file
            catch (java.io.IOException e) {
                System.out.println( "File could not be closed: " + "\n" + e + "\n");
                e.printStackTrace();
            }
        }

    }
}