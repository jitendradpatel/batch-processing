package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class AccountItemProcessor implements ItemProcessor<Account, Account> {

    private static final Logger log = LoggerFactory.getLogger(AccountItemProcessor.class);

    @Override
    public Account process(final Account account) throws Exception {
        Account transformedAccount = new Account();
        transformedAccount.setFiller(account.getFiller());
        log.info("Converted!");
        return transformedAccount;
    }
}
