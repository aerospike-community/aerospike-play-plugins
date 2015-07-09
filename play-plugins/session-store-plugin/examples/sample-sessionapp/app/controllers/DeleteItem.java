package controllers;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import com.aerospike.session.CheckAndSetOperation;
@RequiredArgsConstructor
public class DeleteItem implements CheckAndSetOperation{
	private final ShoppingItem toDelete;

	@Override
	public Map<String, Object> execute(Map<String, Object> currentValues) {
		if(!currentValues.containsKey("shopping-list")){
			return currentValues;
		}
		@SuppressWarnings("unchecked")
		List<ShoppingItem> mylist = (List<ShoppingItem>) currentValues.get("shopping-list");

		mylist.remove(toDelete);
		return currentValues;
	}
	
}
