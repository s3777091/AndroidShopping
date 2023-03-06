Introduction:
    Kishop is shop but have 0 product. While i buying samsung s22 phone i went to cellphone S to check
    price it about 26 million thousand dong however while i went outside and asking for another
    shop it only 22 million thousand dong. So, kishop come out it only know product of customer want then
    while customer send order admin will check product and go to shop have small price of that product to buy
    then i will send back to customer buy that product.

    => Condition: take time to buy however using technology nothing is impossible.

Account Testing:
    case test 1:
    => TK: huynhdactandat@gmail.com
    => Mk: Dathuynh1909

    case test 2:
    => TK: dathuynhcoolest@gmail.com
    => Mk: Dathuynh1909

Work distribute (s3777091@rmit.edu.vn):
    => login / register.
    => Search everything.
    => all design.
    => product detail.
    => product cart.
    => product history.
    => list of all components.
    => update user profile.
    => control fetch api.
    => product confirm.
    => product payment.
    => product address.
    => buy product in search area.
    => dark mode.
    => database config user.

Technology using:
    For back-end from api:
        => kafka, redis, spring boot, mail gun, postgressql ...
        => hosting in sever control using docker in ubuntu sever.
        => deploy have owner domain at: https://kishop.store/ have ssl.
        => deploy product for kafka at: https://kishop.store:2323.
        => deploy consumer for kafka at: https://kishop.store:2525.

    For front-end:
        => android studio.
        => even bus for control total Money in cart ( have bug).
        => fetch api using Retrofit and RxJava.
        => glider for display image.


Bug:
=> send mail fail so register will have bug you don't have your mail
due to limit sending in mail gun and several problem about they ban my account for sharing.

Wish function:
due to lack of time in this project only work for 1 person so i can adding payment with momo in this project.