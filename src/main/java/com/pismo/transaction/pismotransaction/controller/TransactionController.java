package com.pismo.transaction.pismotransaction.controller;

import com.pismo.transaction.pismotransaction.dto.TransactionCreationRequest;
import com.pismo.transaction.pismotransaction.entity.Transaction;
import com.pismo.transaction.pismotransaction.exception.InvalidTransactionException;
import com.pismo.transaction.pismotransaction.exception.NoRecordFoundException;
import com.pismo.transaction.pismotransaction.service.TransactionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @GetMapping("/")
    @ApiOperation(value = "Get all transaction records")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved")
    })
    public List<Transaction> getAll() {
        return service.getTransactions();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get transaction records for given Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved"),
            @ApiResponse(code = 404, message = "Not found - The record was not found")
    })
    public Transaction getTransaction(@PathVariable Long id) throws NoRecordFoundException {
        return service.getTransaction(id);
    }

    @GetMapping("/account/{id}")
    @ApiOperation(value = "Get all transaction records for given account Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved")
    })
    public List<Transaction> getTransactionForAccount(@PathVariable Long id) {
        return service.getTransactionsForAccount(id);
    }

    @PostMapping(value = "/",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates new  transaction records with the given details")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully added"),
            @ApiResponse(code = 400, message = "Invalid input data - Account not found, Invalid Amount")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction addTransaction(@RequestBody TransactionCreationRequest transaction) throws NoRecordFoundException, InvalidTransactionException {
        return service.addTransaction(transaction);
    }

//    @PutMapping(value = "/{id}",
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.APPLICATION_JSON_VALUE)
//    public Account saveAccount(@RequestBody Account account, @PathVariable Long id) throws DuplicateRecordException, NoRecordFoundException {
//        return service.updateAccount(account, id);
//    }
//
//    @DeleteMapping("/{id}")
//    public void removeAccount(@PathVariable Long id) throws NoRecordFoundException {
//        service.deleteAccount(id);
//    }

}
