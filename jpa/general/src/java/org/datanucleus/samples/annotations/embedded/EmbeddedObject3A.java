/**********************************************************************
Copyright (c) 2016 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
     ...
 **********************************************************************/
package org.datanucleus.samples.annotations.embedded;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class EmbeddedObject3A
{
    String nameA;

    @Embedded
    private EmbeddedObject3B b = new EmbeddedObject3B();

    public void setNameA(String name)
    {
        this.nameA = name;
    }
    public String getNameA()
    {
        return nameA;
    }
    public EmbeddedObject3B getB()
    {
        return b;
    }
    public void setB(EmbeddedObject3B b) 
    {
        this.b = b;
    }
}
