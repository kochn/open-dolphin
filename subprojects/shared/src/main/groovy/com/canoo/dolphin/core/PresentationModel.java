/*
 * Copyright 2012 Canoo Engineering AG.
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

package com.canoo.dolphin.core;

import java.util.List;

public interface PresentationModel extends Observable {
    String DIRTY_PROPERTY = "dirty";

    String getId();

    List<Attribute> getAttributes();

    Attribute getAt(String propertyName);

    Attribute getAt(String propertyName, Tag tag);

    /**
     * Convenience method to get the value of an attribute if it exists or a default value otherwise.
     */
    public int getValue(String attributeName, int defaultValue);

    Attribute findAttributeByPropertyName(String propertyName);

    Attribute findAttributeByPropertyNameAndTag(String propertyName, Tag tag);

    Attribute findAttributeByQualifier(String qualifier);

    Attribute findAttributeById(long id);

    void syncWith(PresentationModel other);

    String getPresentationModelType();

    void addAttribute(Attribute attribute);

    boolean isDirty();
}
