### Price Comparision Report

### Introduction

Used to compare two unenriched datasources of prices where one is being throttled for load bearing arguments. Here two sources of prices a) Bank and b) a Third party are being compared. The Third party source provides the same data as the bank but with a 30 second delay. Any price not updated in the last 30 seconds will not be present in the Third Party output.

#### Assumptions

The bank source is streaming and may provide a continuous stream of data with manageable delay between two streams. As the Third Party source source SENDS ALL DATA at the end of thirty seconds, it will be a burst of data near the end of the 30 second cycle.

The author does not have expousre to listener based environment. Hence he has taken the liberty to feed the listener via a REST call. However the call has been wrapped via another method and hence registering the implemented listener should not be a problem. Author has used System.out in most places. However loggers are the production implementation of choice.

### Approach

The cutoff dataset is derived from the first call is made to the third party listener. The first call indicates that data is ready and is being sent. This first call is published as an event and is picked up by an action listener listening at the Bank API. this event causes the bank api to dump its current queue of prices into a second blocking queue and clear the main queue that is being used to listen.
This implies that the dataset to have calculations performed is created and a queue is being freshly written for the next event.

Similarly another action listener listens to an event that markes the begining of the second stream of data (after the first 30 second interval is elapsed) This event is used to derive the dataset for third party data. Once both these sets are available it is passed to a method that dervies a text based report and outlines the following cases

```bash
BANK_SERVICE_CONNECTION_DOWN : When nothing is received from bank
THIRD_PARTY_SERVICE_CONNECTION_DOWN : No data from third party
THIRD_PARTY_NOT_RECEIVED: Symbols missing in TP but present in bank
PRICE_MISMATCH: Indicates price mismatch in latest prices
PRICE_QUOTE: For symbols where prices match
```
### Class Objects

```python
package com.arbardhan.ms.pricingreport.listener;
BankPriceListenerImpl:
Implementation of the Bank listener. here the Rest api was plugged.
ThirdPartyPriceListenerImpl:
Implementation of the Third Partylistener. With Rest

package com.arbardhan.ms.pricingreport.collector;
BankPricesCollector:
Collects bank prices into a map for calculation and derives latest price
ThirdPartyBankPricesCollector:
Collects third party prices into a map for calculation and derives
the latest price.

com.arbardhan.ms.pricingreport.service;
PriceComparisionService:
Called by an ActionListener when both the maps stated above are ready. 
It derives the String codes to be sent 

com.arbardhan.ms.pricingreport.alert:
AlertServiceImpl:
A dummy implementation of the Alert Service
```
### Tests
Efforts are made to test the alert generation flow.

### Author
Abhishek Roy Bardhan:
roybardhan.abhishek@gmail.com