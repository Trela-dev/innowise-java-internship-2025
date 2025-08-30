package dev.trela.testing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author {
    private Integer id;
    private String name;
    public Author(String name) {
        this.name = name;
    }
    public String toString(){
        return name;
    }
}
