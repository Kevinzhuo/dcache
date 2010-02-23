package org.dcache.services.info.serialisation;

import java.util.Map;

import org.dcache.services.info.base.BooleanStateValue;
import org.dcache.services.info.base.FloatingPointStateValue;
import org.dcache.services.info.base.IntegerStateValue;
import org.dcache.services.info.base.State;
import org.dcache.services.info.base.StateExhibitor;
import org.dcache.services.info.base.StatePath;
import org.dcache.services.info.base.StateVisitor;
import org.dcache.services.info.base.StringStateValue;


/**
 * The SimpleTextSerialiser converts the dCache State tree into a simple String
 * representation.  This representation contains multiple lines, each containing
 * the path to the metric followed by a colon, then a space, the value, then
 * finally the metric type (in square brackets).
 * <p>
 * NB, instances of this Class are not thread-safe: the caller is responsible for
 * ensuring no concurrent calls to serialise().
 *
 * @see PrettyPrintTextSerialiser
 *
 * @author Paul Millar <paul.millar@desy.de>
 */
public class SimpleTextSerialiser implements StateVisitor, StateSerialiser {

    public static final String NAME = "simple";

    private static final String LIST_TYPE = "List item";

    private StringBuilder _result = new StringBuilder();
    private StatePath _lastStateComponentPath=null;
    private StatePath _startPath;
    private final StateExhibitor _exhibitor;

    public SimpleTextSerialiser( StateExhibitor exhibitor) {
        _exhibitor = exhibitor;
    }

    public String serialise( StatePath start) {
        _result = new StringBuilder();
        _startPath = start;

        if( start != null) {
            _result.append( start.toString());
            _result.append(">\n");
        }

        _exhibitor.visitState( this, start);

        return _result.toString();
    }

    public String serialise() {
        return serialise( null);
    }


    public String getName() {
        return NAME;
    }

    public void visitBoolean(StatePath path, BooleanStateValue value) {
        outputMetric( path, value.toString(), value.getTypeName());
        _lastStateComponentPath = path;
    }

    public void visitFloatingPoint(StatePath path, FloatingPointStateValue value) {
        outputMetric( path, value.toString(), value.getTypeName());
        _lastStateComponentPath = path;
    }

    public void visitInteger(StatePath path, IntegerStateValue value) {
        outputMetric( path, value.toString(), value.getTypeName());
        _lastStateComponentPath = path;
    }

    public void visitString(StatePath path, StringStateValue value) {
        outputMetric( path, "\""+value+"\"", value.getTypeName());
        _lastStateComponentPath = path;
    }

    public void visitCompositePreDescend(StatePath path, Map<String, String> metadata) {
        _lastStateComponentPath = path;
    }
    public void visitCompositePostDescend(StatePath path, Map<String, String> metadata) {

        // If we just traversed a path without it containing any elements, treat it as a list.
        if( path != null && path.equals(_lastStateComponentPath) && !path.isSimplePath()) {
            String type = LIST_TYPE;

            if( metadata != null) {
                String className = metadata.get( State.METADATA_BRANCH_CLASS_KEY);
                if( className != null)
                    type = className;
            }
            outputMetric( path, "", type);
        }
    }

    public void visitCompositePreSkipDescend(StatePath path, Map<String, String> metadata) {}
    public void visitCompositePostSkipDescend(StatePath path, Map<String, String> metadata) {}

    /**
     * Output a single line, corresponding to a metric value.
     * @param path  The StatePath for this metric
     * @param metricValue the String representing this metric's current value.
     * @param metricType the type of metric.
     */
    private void outputMetric( StatePath path, String metricValue, String metricType) {

        if( _startPath != null)
            _result.append( "  ");

        if( path != null)
            _result.append( path.toString( _startPath));

        _result.append( ":  ");
        _result.append( metricValue);
        _result.append( " [");
        _result.append( metricType);
        _result.append( "]\n");
    }
}
