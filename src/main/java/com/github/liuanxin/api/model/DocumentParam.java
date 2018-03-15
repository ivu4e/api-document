package com.github.liuanxin.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@NoArgsConstructor
@Accessors(chain = true)
public class DocumentParam {

    private String name;

    private String dataType;

    private String paramType;

    private boolean must;

    private String desc;

    private String example;
}
