
package com.tea.model;

import com.tea.network.Message;

public interface IChat {

    public void read(Message ms);

    public void wordFilter();

    public void send();
}
