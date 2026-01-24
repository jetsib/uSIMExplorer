package simexplorer.efTools;

import java.util.HashMap;
import java.util.Map;

public final class NodeDef {
    private static final Map<String, NodeDef> BY_NAME = new HashMap<>();

    public final String name;
    public final NodeType type;
    public final int fid;
    public final byte[] aid;

    private NodeDef(String name, NodeType type, int fid, byte[] aid) {
        this.name = name;
        this.type = type;
        this.fid = fid;
        this.aid = aid;
        BY_NAME.put(name, this);
    }

    public static NodeDef mf(String name, int fid) { return new NodeDef(name, NodeType.MF, fid, null); }
    public static NodeDef df(String name, int fid) { return new NodeDef(name, NodeType.DF, fid, null); }
    public static NodeDef ef(String name, int fid) { return new NodeDef(name, NodeType.EF, fid, null); }
    public static NodeDef adf(String name, byte[] aid) { return new NodeDef(name, NodeType.ADF, -1, aid); }
    public static byte[] fidBytesByName(String name) {
        NodeDef n = BY_NAME.get(name);
        if (n == null) return null;

        if (n.type == NodeType.ADF) return n.aid;
        if (n.fid < 0) return null;

        return new byte[]{
                (byte)((n.fid >> 8) & 0xFF),
                (byte)(n.fid & 0xFF)
        };
    }

    public static NodeDef byName(String name) { return BY_NAME.get(name); }

    @Override
    public String toString() { return name; }
}
