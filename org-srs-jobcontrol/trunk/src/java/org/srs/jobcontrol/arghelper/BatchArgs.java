
package org.srs.jobcontrol.arghelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PatternOptionBuilder;

/**
 * Helper class to parse command line options, and optionally 
 * @author bvan
 */
public abstract class BatchArgs {
    protected static final Pattern tokenizer = Pattern.compile("\\s*(?:\"([^\"]*)\"|(\\S+))");
    protected Options opts;
    
    public BatchArgs(String optionPattern){
        opts = PatternOptionBuilder.parsePattern( optionPattern );
    }
    
    public abstract String getDirective();
    public Options getOptions(){ return opts; }
    
    public String getOptions(String options){
        StringBuilder sb = new StringBuilder();
        try {
            CommandLine cl = new BasicParser().parse(opts, tokenizeExtraOption( options ), false);
            for(Option o: cl.getOptions()){
                sb.append( String.format("%s -%s %s\n", getDirective(), o.getOpt(), o.getValue("")) );
            }
        } catch(ParseException ex) {
            Logger.getLogger( LSFArgs.class.getName() ).log( Level.SEVERE, null, ex );
        }
        return sb.toString();
    }
    
    protected String[] tokenizeExtraOption(String string){
        List<String> result = new ArrayList<String>();
        Matcher matcher = tokenizer.matcher( string );
        while(matcher.find()){
            result.add( matcher.group( 2 ) == null ? matcher.group( 1 ) : matcher.group( 2 ) );
        }
        return result.toArray( new String[0]);
    }

}
