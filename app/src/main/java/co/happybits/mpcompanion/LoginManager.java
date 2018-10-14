//package co.happybits.mpcompanion;
//
//import co.happybits.hbmx.mp.ApplicationIntf;
//import co.happybits.hbmx.mp.RegistrationSendType;
//import co.happybits.hbmx.mp.StartRegistrationResponse;
//import co.happybits.hbmx.mp.StartRegistrationStatus;
//import co.happybits.hbmx.mp.UserManagerIntf;
//import co.happybits.hbmx.mp.VerifyRegistrationResponse;
//import co.happybits.hbmx.mp.VerifyRegistrationStatus;
//import co.happybits.hbmx.tasks.Task;
//import co.happybits.hbmx.tasks.TaskObservable;
//
//public class LoginManager {
//
//    static TaskObservable<Boolean> login(final String phone, final String countryCode) {
//
//        // phone: the local phone number including area code (e.g. 2065551234)
//        // countryCode: the two letter country identifier (e.g. "US")
//
//        return new Task<Boolean>() {
//
//            @Override
//            public Boolean access() {
//
//                UserManagerIntf userManager = ApplicationIntf.getUserManager();
//                if (userManager.isRegistered()) {
//                    return true;
//                }
//
//                StartRegistrationResponse startResponse = userManager.startRegistrationForPhone(phone, countryCode, RegistrationSendType.SMS);
//                if (startResponse.getStatus() != StartRegistrationStatus.NO_ERROR) {
//                    return false;
//                }
//
//                VerifyRegistrationResponse verifyResponse = userManager.autoVerify();
//                if (verifyResponse.getStatus() != VerifyRegistrationStatus.NO_ERROR) {
//                    return false;
//                }
//
//                return userManager.isRegistered();
//            }
//        }.submit();
//    }
//}
