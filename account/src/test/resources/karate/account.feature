Feature: Account API

  Scenario: Get all accounts
    * url 'http://localhost:8081/api'
    Given path 'cuentas'
    When method GET
    Then status 200

  Scenario: Test account operations
    # Get any client
    * url 'http://localhost:8080/api'
    Given path 'clientes'
    When method GET
    Then status 200
    * def clientId = response[0].clientId
    # Create
    * url 'http://localhost:8081/api'
    Given path 'cuentas'
    And request { type: 'CHECKING', initialBalance: 1000, active: true, clientId: '#(clientId)' }
    When method POST
    Then status 201
    * def accountNumber = response.accountNumber
    And match response == { accountNumber: '#(accountNumber)', type: 'CHECKING', initialBalance: 1000, active: true, clientId: '#(clientId)' }
    # Get
    Given path 'cuentas', accountNumber
    When method GET
    Then status 200
    And match response == { accountNumber: '#(accountNumber)', type: 'CHECKING', initialBalance: 1000, active: true, clientId: '#(clientId)' }
    # Update
    Given path 'cuentas', accountNumber
    And request { type: 'SAVINGS', initialBalance: 1000, active: true, clientId: '#(clientId)' }
    When method PUT
    Then status 200
    And match response == { accountNumber: '#(accountNumber)', type: 'SAVINGS', initialBalance: 1000, active: true, clientId: '#(clientId)' }
    # Partial Update
    Given path 'cuentas', accountNumber
    And request { active: false }
    When method PATCH
    Then status 200
    And match response.active == false
    # Delete
    Given path 'cuentas', accountNumber
    When method DELETE
    Then status 200
