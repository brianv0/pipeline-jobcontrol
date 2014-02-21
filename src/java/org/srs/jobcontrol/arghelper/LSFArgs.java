
package org.srs.jobcontrol.arghelper;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 *
 * @author bvan
 */
public class LSFArgs extends BatchArgs {
    final static String optionPattern = "q:n:W:o:e:M:P:m:E:GHr";
    
    class QuotedOption extends Option {
        public QuotedOption(String opt){
            super( opt, "" );
            setArgs( 1 );
        }
        @Override
        public String getValue(){
            return '"' + super.getValue() + '"';
        }
    };

    public LSFArgs(){
        super(optionPattern);
        getOptions().addOption( OptionBuilder.hasArg().create("sp") );
        getOptions().addOption( OptionBuilder.create("rn") );
        getOptions().addOption( OptionBuilder.hasArg(false).create("ar") );
        getOptions().addOption( new QuotedOption("R") );
    }

    public String getDirective(){
        return "#BSUB";
    }
    
}
