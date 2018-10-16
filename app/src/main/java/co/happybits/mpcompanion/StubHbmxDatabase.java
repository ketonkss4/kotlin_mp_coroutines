//package co.happybits.mpcompanion;
//
//import java.util.ArrayList;
//
//import javax.annotation.Nonnull;
//
//import co.happybits.hbmx.mp.BackoffTimingIntf;
//import co.happybits.hbmx.mp.BackoffTimingTableIntf;
//import co.happybits.hbmx.mp.ConversationIntf;
//import co.happybits.hbmx.mp.ConversationTableIntf;
//import co.happybits.hbmx.mp.ConversationUserIntf;
//import co.happybits.hbmx.mp.ConversationUserTableIntf;
//import co.happybits.hbmx.mp.ImageUploadIntf;
//import co.happybits.hbmx.mp.ImageUploadTableIntf;
//import co.happybits.hbmx.mp.ImageUploadType;
//import co.happybits.hbmx.mp.MessageIntf;
//import co.happybits.hbmx.mp.MessageTableIntf;
//import co.happybits.hbmx.mp.MicroserviceType;
//import co.happybits.hbmx.mp.RetryableApiCallIntf;
//import co.happybits.hbmx.mp.RetryableApiCallTableIntf;
//import co.happybits.hbmx.mp.SupportRequestIntf;
//import co.happybits.hbmx.mp.SupportRequestTableIntf;
//import co.happybits.hbmx.mp.UserIntf;
//import co.happybits.hbmx.mp.UserTableIntf;
//import co.happybits.hbmx.mp.VideoIntf;
//import co.happybits.hbmx.mp.VideoTableIntf;
//import co.happybits.hbmx.mp.VideoUploadIntf;
//import co.happybits.hbmx.mp.VideoUploadTableIntf;
//
//public class StubHbmxDatabase {
//
//    public static class ConversationTable implements ConversationTableIntf {
//
//        @Override
//        public ConversationIntf queryByXid(@Nonnull String xid) {
//            return null;
//        }
//
//        @Override
//        public ConversationIntf queryOrCreateByXid(@Nonnull String xid) {
//            return null;
//        }
//
//        @Override
//        public ConversationIntf queryOrCreateByRecipient(UserIntf user) {
//            return null;
//        }
//
//        @Override
//        public ConversationIntf createGroup(@Nonnull ArrayList<UserIntf> members) {
//            return null;
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<ConversationIntf> queryAllNeedingPost(long minRetryTime) {
//            return new ArrayList<ConversationIntf>();
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<ConversationIntf> queryActive() {
//            return new ArrayList<ConversationIntf>();
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<ConversationIntf> queryAllUnread() {
//            return new ArrayList<ConversationIntf>();
//        }
//
//        @Override
//        public long queryUnreadOneOnOneConversationCount() {
//            return 0;
//        }
//
//        @Override
//        public long queryUnreadGroupConversationCount() {
//            return 0;
//        }
//
//        @Override
//        public long queryUnreadTotalConversationCount() {
//            return 0;
//        }
//
//        @Override
//        public long queryUnreadConversationsBadgeCount() {
//            return 0;
//        }
//
//        @Override
//        public long queryHomeScreenInviteCount() {
//            return 0;
//        }
//    }
//
//    public static class UserTable implements UserTableIntf {
//
//        @Override
//        public UserIntf queryByXid(@Nonnull String xid) {
//            return null;
//        }
//
//        @Override
//        public UserIntf queryByPhone(@Nonnull String phone) {
//            return null;
//        }
//
//        @Override
//        public UserIntf queryOrCreateByPhone(@Nonnull String phone) {
//            return null;
//        }
//
//        @Override
//        public UserIntf queryOrCreateByPhoneOrXID(String phone, String xid) {
//            return null;
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<UserIntf> queryContactsByQuality() {
//            return new ArrayList<UserIntf>();
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<UserIntf> queryAllNeedingPatch(long minRetryTime) {
//            return new ArrayList<UserIntf>();
//        }
//    }
//
//    public static class ConversationUserTable implements ConversationUserTableIntf {
//
//        @Override
//        public ConversationUserIntf queryOrCreateByConversationAndUser(ConversationIntf conversations, UserIntf user) {
//            return null;
//        }
//
//        @Override
//        public void updateLastWatchedMessage(long timestamp, ConversationIntf conversations, UserIntf user) {
//
//        }
//    }
//
//    public static class MessageTable implements MessageTableIntf {
//
//        @Override
//        public MessageIntf createVideoMessage(ConversationIntf conversations, VideoIntf video, UserIntf creator) {
//            return null;
//        }
//
//        @Override
//        public MessageIntf queryOrCreateByXid(@Nonnull String xid, ConversationIntf conversations, UserIntf creator) {
//            return null;
//        }
//
//        @Override
//        public MessageIntf queryByXid(@Nonnull String xid) {
//            return null;
//        }
//
//        @Override
//        public MessageIntf queryFirstAfter(MessageIntf message) {
//            return null;
//        }
//
//        @Override
//        public MessageIntf queryFirstAfterIncludingNotes(MessageIntf message) {
//            return null;
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<MessageIntf> queryOldestUnwatched(ConversationIntf conversations, int limit) {
//            return new ArrayList<MessageIntf>();
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<MessageIntf> queryAllNeedingPut(long minRetryTime) {
//            return new ArrayList<MessageIntf>();
//        }
//    }
//
//    public static class VideoTable implements VideoTableIntf {
//
//        @Override
//        public VideoIntf queryByXid(@Nonnull String xid) {
//            return null;
//        }
//
//        @Override
//        public VideoIntf queryOrCreateByXid(@Nonnull String xid) {
//            return null;
//        }
//    }
//
//    public static class ImageUploadTable implements ImageUploadTableIntf {
//
//        @Override
//        public ImageUploadIntf createByXid(@Nonnull String xid, @Nonnull ImageUploadType imageUploadType, @Nonnull String contentType, UserIntf user, ConversationIntf conversations) {
//            return null;
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<ImageUploadIntf> queryAllByMinRetryTime(long minRetryTime) {
//            return new ArrayList<ImageUploadIntf>();
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<ImageUploadIntf> queryAllByTypeAndUser(@Nonnull ImageUploadType imageUploadType, UserIntf user) {
//            return new ArrayList<ImageUploadIntf>();
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<ImageUploadIntf> queryAllByTypeAndConversation(@Nonnull ImageUploadType imageUploadType, ConversationIntf conversations) {
//            return new ArrayList<ImageUploadIntf>();
//        }
//    }
//
//    public static class BackoffTimingTable implements BackoffTimingTableIntf {
//
//        @Override
//        public BackoffTimingIntf create() {
//            return null;
//        }
//    }
//
//    public static class SupportRequestTable implements SupportRequestTableIntf {
//
//        @Override
//        public SupportRequestIntf create(@Nonnull String emailAddress, @Nonnull String requestBody, MessageIntf message) {
//            return null;
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<SupportRequestIntf> queryAllByMinRetryTime(long minRetryTime) {
//            return new ArrayList<SupportRequestIntf>();
//        }
//    }
//
//    public static class RetryableApiCallTable implements RetryableApiCallTableIntf {
//
//        @Override
//        public RetryableApiCallIntf create(@Nonnull String request, @Nonnull String body, String filePath, @Nonnull MicroserviceType microServiceType) {
//            return null;
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<RetryableApiCallIntf> queryAllByMinRetryTime(long minRetryTime) {
//            return new ArrayList<RetryableApiCallIntf>();
//        }
//
//        @Nonnull
//        @Override
//        public ArrayList<RetryableApiCallIntf> queryAllForRequest(@Nonnull String request, @Nonnull MicroserviceType microserviceType) {
//            return new ArrayList<RetryableApiCallIntf>();
//        }
//    }
//
//    public static class VideoUploadTable implements VideoUploadTableIntf {
//
//        @Override
//        public VideoUploadIntf queryOrCreateForMessage(MessageIntf message, @Nonnull String filePath) {
//            return null;
//        }
//
//        @Override
//        public VideoUploadIntf getMessageToUpload(String conversationID) {
//            return null;
//        }
//    }
//}
