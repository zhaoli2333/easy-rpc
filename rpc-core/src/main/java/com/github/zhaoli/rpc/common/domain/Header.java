/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zhaoli.rpc.common.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Header {
    private int type;
    private int version;
    private int serializerType;

    public Header() {}
    public Header(int type, int version, int serializerType) {
        this.type = type;
        this.version = version;
        this.serializerType = serializerType;
    }

    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSerializerType() {
        return serializerType;
    }

    public void setSerializerType(int serializerType) {
        this.serializerType = serializerType;
    }
}

