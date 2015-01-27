package com.tiantiandou.yasf.service;

import io.netty.buffer.ByteBuf;

import com.tiantiandou.yasf.message.YasfSerializable;
import com.tiantiandou.yasf.message.utils.ByteBufUtils;

/***
 * Service的定义 TODO 类说明
 * 
 * @author <a href="mailto:hukui@alibaba-inc.com">hukui</a>
 * @version 1.0
 * @since 2014年12月26日
 */
public class YasfServiceEntity implements YasfSerializable {
    private static final long serialVersionUID = 6266093305255206065L;
    private String name; // 方法全称:com.xxx.xxxx.className
    private String version;
    private String provider;

    public void initFrom(ByteBuf buf) {
        name = ByteBufUtils.readString(buf);
        version = ByteBufUtils.readString(buf);
        provider = ByteBufUtils.readString(buf);
    }

    public int fillTo(ByteBuf buf) {
        int length = ByteBufUtils.writeString(name, buf);
        length += ByteBufUtils.writeString(version, buf);
        length += ByteBufUtils.writeString(provider, buf);
        return length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((provider == null) ? 0 : provider.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        YasfServiceEntity other = (YasfServiceEntity) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (provider == null) {
            if (other.provider != null) {
                return false;
            }
        } else if (!provider.equals(other.provider)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }
}
