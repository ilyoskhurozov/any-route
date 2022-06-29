package uz.ilyoskhurozov.anyroute.util;

import java.util.Map;

public record TopologyData(String name, String source, String target,
                           Map<String, Map<String, Boolean>> isConnectedTable) {
}
