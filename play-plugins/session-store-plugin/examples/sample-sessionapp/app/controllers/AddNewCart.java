/*
 * Copyright (C) 2015 Aeroshift Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import com.aerospike.session.CheckAndSetOperation;
@RequiredArgsConstructor
public class AddNewCart implements CheckAndSetOperation{
	private final ShoppingItem toAdd;

	public Map<String, Object> execute(Map<String, Object> currentValues) {
		if(!currentValues.containsKey("shopping-list")) {
			currentValues.put("shopping-list", new ArrayList<ShoppingItem>());
		}
		@SuppressWarnings("unchecked")
		List<ShoppingItem> mylist = (List<ShoppingItem>) currentValues.get("shopping-list");
		mylist.add(toAdd);
		return currentValues;
	}
	
	
}
