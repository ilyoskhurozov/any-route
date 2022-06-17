package uz.ilyoskhurozov.anyroute.util;

import java.util.Map;

public class TopologyData {
    public final String name;
    public final String source;
    public final String target;
    public final Map<String, Map<String, Boolean>> isConnectedTable;

    public TopologyData(
            String name,
            String source,
            String target,
            Map<String, Map<String, Boolean>> isConnectedTable
    ) {
        this.name = name;
        this.source = source;
        this.target = target;
        this.isConnectedTable = isConnectedTable;
    }
}
