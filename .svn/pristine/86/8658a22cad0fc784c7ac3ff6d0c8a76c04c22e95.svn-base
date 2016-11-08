package kobdig.gui;

import java.io.File;

/**
 * A filter for the file chooser to visualize KOBDIG agent program files only.
 * A KOBDIG agent file is recognized by the extension ".apl".
 * 
 * @author Andrea G. B. Tettamanzi
 */
public class AgentFileFilter extends KobdigFileFilter
{
    /**
     * Accept all directories and all KOBDIG agent files.
     */
    @Override
    public boolean accept(File f)
    {
        if(f.isDirectory())
            return true;

        String extension = getExtension(f);
        if(extension!=null)
            return extension.equals("apl");

        return false;
    }

    /**
     * The description of this filter
     */
    @Override
    public String getDescription()
    {
        return "KOBDIG agent program files";
    }
}
