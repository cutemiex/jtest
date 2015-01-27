package com.tiantiandou.yasf.service.consumer;

/****
 * 类说明 访问远程服务的地址
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2015年1月6日
 */
public class YasfServiceEndpoint {
    private String host;

    private short port;

    public YasfServiceEndpoint(String host, short port) {
        super();
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public short getPort() {
        return port;
    }

    public void setPort(short port) {
        this.port = port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        YasfServiceEndpoint other = (YasfServiceEndpoint) obj;
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        return true;
    }
}
