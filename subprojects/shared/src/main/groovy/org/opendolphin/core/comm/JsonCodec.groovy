/*
 * Copyright 2012-2013 Canoo Engineering AG.
 *
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

package org.opendolphin.core.comm

import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.Tag
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Log

@Log
class JsonCodec implements Codec {

    @Override
    String encode(List<Command> commands) {
        def content = commands.collect { Command cmd ->
            log.finest "encoding command $cmd"
            def entry = cmd.properties
            ['class','metaClass'].each { entry.remove it }
            entry.className = cmd.class.name        // todo dk: is this needed?
            entry.each { key, value ->              // prepare against invalid entries
                if (value instanceof List) {        // some commands may have collective values
                    for (Map entryMap in value) {
                        entryMap.each { entryKey, entryValue ->
                            entryMap[entryKey] = BaseAttribute.checkValue(entryValue)
                    }   }
                } else if (value instanceof Map) {  // DataCommand has map content
                    value.each { entryKey, entryValue ->
                        value[entryKey] = BaseAttribute.checkValue(entryValue)
                    }
                } else {
                    entry[key] = BaseAttribute.checkValue(value)
            }   }
            entry
        }
        JsonBuilder builder = new JsonBuilder(content)
        builder.toString()
    }

    @Override
    List<Command> decode(String transmitted) {
        def result = new LinkedList()
        def got = new JsonSlurper().parseText(transmitted)
        got.each { cmd ->
            Command responseCommand = Class.forName(cmd['className']).newInstance()
            cmd.each { key, value ->
                if (key == 'className') return
                if (key == 'id' && !(responseCommand instanceof NamedCommand)) return // set id only for NamedCommand
                if (key == 'attributeId') value = value.toLong()
                if (key == 'tag') value = Enum.valueOf(Tag, value)
                responseCommand[key] = value
            }
            log.finest "decoded command $responseCommand"
            result << responseCommand
        }
        return result
    }
}
