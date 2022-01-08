package fr.miage.revolut.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchCriteria {

    private String key;
    private String operation;
    private Object value;

    public boolean isOrPredicate() {
        return  operation.equals("|");
    }
}
