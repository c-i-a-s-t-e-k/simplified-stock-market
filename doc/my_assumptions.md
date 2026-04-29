# My Assumptions
on POST /stocks
 * update bank status
 * wallets allways have to have ability to sell their stock so if client send a POST /stock such as some wallet may not sell a stock the endpoind with anser with bad Request 400

on GET /Wallet
 * do not create new wallet if request ask of wallet_id that it never seen (404 error)