/*
 * Copyright 2016 Red Hat, Inc.
 * <p>
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package io.fabric8.funktion.example;

import java.util.stream.Collectors;

import org.apache.camel.Header;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.springframework.beans.factory.annotation.Value;

public class Main {
    final static DMNRuntime runtime;
    static {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kContainer = kieServices.getKieClasspathContainer();
        runtime = kContainer.newKieSession().getKieRuntime(DMNRuntime.class);
    }
    
    public Object main(String body, @Header("model") String model, @Header("my_input") String my_input) {
        if (model != null) {
            if ( my_input == null) {
                return "missing URL param 'my_input'";
            }
            
            DMNModel dmnModel = runtime.getModels().stream().filter(m -> m.getName().equals(model)).findFirst().get();
            
            DMNContext dmnContext = runtime.newContext();
            dmnContext.set("My Input", my_input);
            
            DMNResult dmnResult = runtime.evaluateAll(dmnModel, dmnContext);
            
            System.out.println(dmnResult.getContext().toString());
            
            return dmnResult.getContext().toString();
        } else {
            return "Use ?model=<name> by selecting from these models: " + runtime.getModels().stream().map(DMNModel::getName).collect(Collectors.joining(",")) + ".";
        }
    }

}
    