package mn.frd.yeahKarma.database;

import java.util.HashMap;
import java.util.Map;

public class MySQLDatabase extends Database {
	private String hostname = "";
	private String portnmbr = "";
	private String username = "";
	private String password = "";
	private String database = "";
    private Integer maxConnections = 5;
    private Integer startupConnections = 2;

    //Connection pool
    private final HashMap<PooledConnection, Boolean> isConnectionInUse = new HashMap<PooledConnection, Boolean>();

	public MySQLDatabase(String hostname, String portnmbr, String database, String username, String password, Integer maxConnections) {
		super();
		this.hostname = hostname;
		this.portnmbr = portnmbr;
		this.database = database;
		this.username = username;
		this.password = password;
        this.maxConnections = maxConnections;

        setupPool();
	}

    public synchronized void resetPooledConnection(PooledConnection connection) {
        synchronized (isConnectionInUse) {
            isConnectionInUse.put(connection, false);
        }
    }

    /**
     * Setup all pooled Connections
     */
    private void setupPool() {
        for(Integer i = 0; i < startupConnections; i++) {
            PooledConnection pooledConnection = new PooledConnection(this);
            pooledConnection.connect(this.hostname, this.portnmbr, this.database, this.username, this.password);
            isConnectionInUse.put(pooledConnection, false);
        }
    }

    /**
     * Get a free connection or try to build a new one
     *
     * @return
     */
	public synchronized PooledConnection getConnection() {
        synchronized (isConnectionInUse) {
            for(Map.Entry<PooledConnection, Boolean> connectionBooleanEntry : isConnectionInUse.entrySet()) {
                if(!connectionBooleanEntry.getValue()) {
                    isConnectionInUse.put(connectionBooleanEntry.getKey(), true);
                    return connectionBooleanEntry.getKey();
                }
            }

            //No free connection => Check if we can create a new one
            if(isConnectionInUse.size() < maxConnections) {
                PooledConnection pooledConnection = new PooledConnection(this);
                pooledConnection.connect(this.hostname, this.portnmbr, this.database, this.username, this.password);
                isConnectionInUse.put(pooledConnection, true);

                return pooledConnection;
            } else {
                throw new RuntimeException("Requested a Connection but the Pool is busy");
            }
        }
	}
}