package com.zxl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxl.mapper.AddressBookMapper;
import com.zxl.pojo.AddressBook;
import com.zxl.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：xxx
 * @description：TODO
 * @date ：2024/02/08 21:14
 */
@Service
@Slf4j
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
}
