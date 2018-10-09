package com.d.music.transfer.manager.pipe;

import com.d.music.component.greendao.bean.TransferModel;

/**
 * SongPipe
 * Created by D on 2018/10/10.
 */
public class MVPipe extends Pipe {

    public MVPipe() {
        init();
    }

    @Override
    public void init() {
        for (int i = 0; i < 30; i++) {
            TransferModel model = new TransferModel();
            model.state = i % 2 == 1 ? 1 : 0;
            mList.add(model);
        }
    }
}
