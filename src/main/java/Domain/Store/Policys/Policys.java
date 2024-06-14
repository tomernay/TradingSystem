package Domain.Store.Policys;

import Utilities.Response;

public class Policys {


    public Response<Boolean> addDiscountPolicy() {
        return new Response<>(true, null, true);
    }
}
