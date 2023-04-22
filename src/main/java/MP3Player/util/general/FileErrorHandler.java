package MP3Player.util.general;

import java.io.File;
import java.io.FileNotFoundException;

public class FileErrorHandler
{
    static String validationResponse = "somethings wrong, try a quick restart";

    public static int fileCheck(String filePath)
    {
        System.out.println(filePath);

        //check file path
        if(validateFilePath(filePath) != 0)
        {
            System.out.println(validationResponse);
            return 1;
        } else validationResponse = "Filepath found, checking the type now...";

        //check file type
        if(validateFileType(filePath) != 0)
        {
            System.out.println(validationResponse);
            return 1;
        } validationResponse = "The file type is supported, please enjoy your song!";

        //file is all good
        System.out.println(validationResponse);
        return 0;
    }

    //checks if the file type is accepted
    public static int validateFileType(String filePath)
    {
        //maybe make an array to store valid file types to allow users to add ones they know work?
        //would then loop through the array checking instead of switch

        //0 = valid, 1 = invalid, 2 = unknown
        int validity = 0;

        //first ensure correct file type
        switch (filePath.toLowerCase().substring((filePath.length()-4)))
        {
            //for accepted file types do nothing (validity = 0)
            case ".mp3":
                break;
            case ".mp4":
                break;
            case ".m4a":
                break;
            case ".wav":
                break;
            case "mpeg":
                break;

            //known wrong file types (validity = 1)
            case ".exe":
                validity = 1;
                break;
            case "jpeg":
                validity = 1;
                break;
            case ".png":
                validity = 1;
                break;

            //unhandled file types (validity = 2)
            default:
                validity = 2;
                break;
        }

        //react appropriately to the validity of the file
        switch (validity)
        {
            case 0: //everything is fine, so do nothing
                break;
            case 1: //file is invalid prompt for new filepath
                validationResponse = "This file can't be played, please try another path";
                return 1;
            case 2: //file is unknown prompt to proceed with knowledge
                validationResponse = "This file type wasn't recognized, continue at your own risk";
                return 2;
        }

        return 0;
    }

    //checks if the file path can be found
    public static int validateFilePath(String filePath)
    {
        try
        {
            File newFile = new File(filePath);

            if(newFile.exists() == false)
            {
                validationResponse = "Sorry this file path couldn't be found, please check for typos";
                return 1;
            }
        }
        catch (Exception e)
        {
            validationResponse = "Sorry this file path couldn't be found, please check for typos";
            return 2;
        }

        return 0;
    }
}
