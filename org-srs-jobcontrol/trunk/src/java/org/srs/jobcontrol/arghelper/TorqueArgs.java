
package org.srs.jobcontrol.arghelper;

import org.apache.commons.cli.OptionBuilder;

/**
 * 
 * @author bvan
 */
public class TorqueArgs extends BatchArgs {
    final static String optionPattern = "q:l:o:e:j:N:W:r:V";
    
    public TorqueArgs(){
        super( optionPattern );
        getOptions().addOption( OptionBuilder.create("rn") );
    }

    @Override
    public String getDirective(){
        return "#PBS";
    }
}
