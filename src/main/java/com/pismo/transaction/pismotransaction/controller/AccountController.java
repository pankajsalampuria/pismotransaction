package com.pismo.transaction.pismotransaction.controller;

import com.pismo.transaction.pismotransaction.entity.Account;
import com.pismo.transaction.pismotransaction.exception.DuplicateRecordException;
import com.pismo.transaction.pismotransaction.exception.NoRecordFoundException;
import com.pismo.transaction.pismotransaction.service.AccountService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    @GetMapping("/")
    @ApiOperation(value = "Fetch all account record")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful fetch")
    })
    public List<Account> getAll() {
        return service.getAccounts();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Fetch  account record for given Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful fetch"),
            @ApiResponse(code = 404, message = "Not found - account not found")
    })
    public Account getAccount(@PathVariable Long id) throws NoRecordFoundException {
        return service.getAccount(id);
    }

    @PostMapping(value = "/",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Creates new account record with the given details")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully added"),
            @ApiResponse(code = 400, message = "Invalid input data - Duplicate account")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public Account addAccount(@RequestBody Account account) throws DuplicateRecordException {
        return service.addAccount(account);
    }

    @PutMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates existing account records with the given details")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "Successfully updated"),
            @ApiResponse(code = 400, message = "Invalid input data - Account not found, Duplicate account")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Account saveAccount(@RequestBody Account account, @PathVariable Long id) throws DuplicateRecordException, NoRecordFoundException {
        return service.updateAccount(account, id);
    }

//    @DeleteMapping("/{id}")
//    @ApiOperation(value = "Remove existing account records with the given details")
//    @ApiResponses(value = {
//            @ApiResponse(code = 202, message = "Successfully updated"),
//            @ApiResponse(code = 400, message = "Invalid input data - Account not found, Duplicate account")
//    })
//    public void removeAccount(@PathVariable Long id) throws NoRecordFoundException {
//        service.deleteAccount(id);
//    }

}
