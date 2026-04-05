
package com.tea.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Builder
@AllArgsConstructor
public class SmallImage {

    public short id;
    public short x;
    public short y;
    public short w;
    public short h;
}
