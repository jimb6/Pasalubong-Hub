let functions = require('firebase-functions');
let admin = require('firebase-admin');
let algoliasearch = require('algoliasearch');

admin.initializeApp(functions.config().firebase);

const db = admin.firestore();

const ALGOLIA_APP_ID = "KVOHSSARNM";
const ALGOLIA_ADMIN_KEY = "db0c45890c653ad9cd188bb727397716";
const ALGOLIA_INDEX_NAME = "products";



// Set up Algolia.
// The app id and API key are coming from the cloud functions environment, as we set up in Part 1, Step 3.
const algoliaClient = algoliasearch(ALGOLIA_APP_ID, ALGOLIA_ADMIN_KEY);
// Since I'm using develop and production environments, I'm automatically defining 
// the index name according to which environment is running. functions.config().projectId is a default 
// property set by Cloud Functions.

// const collectionIndexName = functions.config().projectId === 'PRODUCTION-PROJECT-NAME' ? 'COLLECTION_prod' : 'COLLECTION_dev';
const collectionIndexName = ALGOLIA_INDEX_NAME;
const collectionIndex = algoliaClient.initIndex(collectionIndexName);


exports.newOrderePlaced = functions.firestore.document('products/{productId}/orders/{orderId}')
    .onCreate((event, context) => {

        const productRef = context.params.productId;
        console.log("Product ID", context.params.productId);
        const orderRef = context.params.orderedId;
        console.log("Order ID", context.params.orderedId);


        let data = {
            id: productRef
        };

        let setDoc = db.collection('products').doc(productRef).collection('orders').doc(orderRef).update(data)
            .then(function () {
                console.log("Document successfully updated!");
                return null;
            })
            .catch(function (error) {
                console.error("Error writing document: ", error);
                return null;
            });
    });

exports.newProductUploaded = functions.firestore.document('products/{productId}')
    .onCreate((event, context) => {
        const productRef = context.params.productId;
        console.log("Product ID", context.params.productId);


        let data = {
            productReference: productRef
        };

        // Add a new document in collection "cities" with ID 'LA'
        return db.collection('products').doc(productRef).update(data)
            .then(function () {
                console.log("Document successfully updated!");
                return null;
            })
            .catch(function (error) {
                console.error("Error writing document: ", error);
                return null;
            });
    });

exports.productOnDelete = functions.firestore.document('products/{productId}/orders/{orderId}')
    .onDelete((event, context) => {

    });


exports.sendOrderNotificationCreate = functions.firestore.document('products/{productId}/orders/{orderId}')
    .onCreate((event, context) => {

        console.log("System: starting ");
        console.log("System: Create");
        console.log("event: ", event);

        const productId = context.params.productId;
        console.log("productId: ", productId);
        const orderId = context.params.orderId;
        console.log("orderId: ", orderId);
        const customerEmail = event.id.customerEmail;
        console.log("Customer email:", customerEmail)

        return admin.firestore().collection("products")
            .doc(productId).collection("orders")
            .doc(orderId).get()
            .then(queryResult => {

                console.log("queryResult: ", queryResult);

                const customer = queryResult.data().user_reference;
                console.log("customer ID: ", customer);
                const seller = queryResult.data().seller_reference;
                console.log("seller ID: ", seller);

                const orderedBy = admin.firestore().collection("users").doc(customer).get();
                console.log("orderedBy: ", orderedBy);
                const shopOwner = admin.firestore().collection("users").doc(seller).get();
                console.log("shopOwner: ", shopOwner);

                let tokens = [];

                return Promise.all([orderedBy, shopOwner])
                    .then(results => {
                        // const fromUserName = result[0].data().email;
                        // const toUserName = result[1].data().email;
                        console.log("result", results);
                        const customerTokenId = results[0].data().messaging_token;
                        const sellerTokenId = results[1].data().messaging_token;
                        console.log("Customer TokenID: ", customerTokenId);
                        console.log("Seller tokenId:", sellerTokenId);

                        let tokens = [customerTokenId, sellerTokenId];

                        for (i = 0; i < tokens.length; i++) {
                            const notificationContent = {
                                data: {
                                    data_type: "data_type_order_product_broadcast",
                                    title: "New order in your shop.",
                                    message: "Order reference: " + orderId,
                                    customer_reference: customer,
                                    seller_reference: seller,
                                    order_refernce: orderId,
                                    transactionType: "Create",
                                }
                            };


                            admin.messaging().sendToDevice(tokens[i], notificationContent)
                                .then(result => {
                                    console.log("Notification sent! ", tokens[i]);
                                    return null;
                                }).catch(function (error) {
                                    console.log("Error sending message:", error);
                                    return null;
                                });
                        }
                        return null;
                    }).catch(function (error) {
                        console.log("Error sending message:", error);
                        return null;
                    });
            }).catch(function (error) {
                console.log("Error sending message:", error);
                return null;
            });
    });

exports.sendOrderNotificationUpdate = functions.firestore.document('products/{productId}/orders/{orderId}')
    .onUpdate(event => {

        console.log("System: starting");
        console.log("event: ", event);
        console.log("event.after: ", event.after);
        console.log("event.after.data: ", event.after.data());
        console.log("ProductID: ", event.after.ref.parent.parent.id);
        console.log("orderId:", event.after.id);

        const productId = event.after.ref.parent.parent.id;
        console.log("productId: ", event.after.id);
        const orderId = event.after.id;
        console.log("orderId: ", orderId);
        const customerEmail = event.after.id.customerEmail;
        console.log("Customer email:", customerEmail)
        //        const dateOrdered = event.after.id.date_ordered;
        //        console.log("Date of ordered: ", dateOrdered);
        //        const productBrand = event.after.id.product.brand;
        //        console.log("productBrand: ", productBrand);
        //        console.log("product")


        return admin.firestore().collection("products")
            .doc(productId).collection("orders")
            .doc(orderId).get()
            .then(queryResult => {

                console.log("queryResult: ", queryResult);

                const customer = queryResult.data().user_reference;
                console.log("customer ID: ", customer);
                const seller = queryResult.data().seller_reference;
                console.log("seller ID: ", seller);

                const orderedBy = admin.firestore().collection("users").doc(customer).get();
                console.log("orderedBy: ", orderedBy);
                const shopOwner = admin.firestore().collection("users").doc(seller).get();
                console.log("shopOwner: ", shopOwner);

                let tokens = [];

                return Promise.all([orderedBy, shopOwner])
                    .then(results => {
                        // const fromUserName = result[0].data().email;
                        // const toUserName = result[1].data().email;
                        console.log("result", results);
                        const customerTokenId = results[0].data().messaging_token;
                        const sellerTokenId = results[1].data().messaging_token;
                        console.log("Customer TokenID: ", customerTokenId);
                        console.log("Seller tokenId:", sellerTokenId);

                        var i;
                        for (i = 0; i < results.length; i++) {
                            const notificationContent = {
                                data: {
                                    data_type: "data_type_order_product_broadcast",
                                    title: "New order in your shop.",
                                    message: "Order reference: " + orderId,
                                    customer_reference: customer,
                                    seller_reference: seller,
                                    order_refernce: orderId,
                                    transactionType: "Update",
                                }
                            };

                            var tokenId;
                            if (i === 0) {
                                tokenId = customerTokenId;
                            } else {
                                tokenId = sellerTokenId;
                            }
                            admin.messaging().sendToDevice(tokenId, notificationContent)
                                .then(result => {
                                    console.log("Notification sent! ", tokenId);
                                    return null;
                                }).catch(function (error) {
                                    console.log("Error sending message:", error);
                                    return null;
                                });
                        }
                        return null;
                    }).catch(function (error) {
                        console.log("Error sending message:", error);
                        return null;
                    });
            }).catch(function (error) {
                console.log("Error sending message:", error);
                return null;
            });
    });

exports.creatingInbox = functions.firestore.document('chatrooms/{roomId}')
    .onCreate((event, context) => {

        console.log("Start event", "Creating new inbox");
        const newValue = snap.data();
        const businessId = newValue.business_id;
        const creatorId = newValue.creator_id;

        let Businessdata = {
            id: businessId,
        }

        return null;

    });

exports.newBusinessRegistered = functions.firestore.document('BUSINESS/{id}')
    .onWrite((event, context) => {

        console.log("Start event", "Creating business");
        const businessEmail = event.after.data().businessEmail;
        console.log("Business Email", businessEmail);
        const businessName = event.after.data().businessName;
        console.log("Business name", businessName);
        const coverUri = event.after.data().coverUri;
        console.log("COVER", coverUri);
        const ownerId = event.after.data().ownerId;
        console.log("OWNER", ownerId);
        const lat = event.after.data().lat;
        console.log("Lat", lat);
        const lng = event.after.data().lng;
        console.log("Lng", lng);

        let data = {
            businessEmail: businessEmail,
            businessName: businessName,
            coverUri: coverUri,
            ownerId: ownerId,
            lat: lat,
            lng: lng,
        };

        var myDB = admin.database();
        var ref = myDB.ref('business/');

        return ref.child(context.params.id).set(data);
    });

exports.newMessageStored = functions.firestore.document('chatrooms/{roomId}/messages/{messageId}')
    .onCreate((event, context) => {
        console.log("System: starting");
        console.log("snapshot: ", change);
        console.log("snapshot.after: ", change);
        console.log("snapshot.after.val(): ", change.val());

        return null;
    });


exports.sendNotification = functions.database.ref('/messages/{messageId}/{documentId}')
    .onCreate((snap, context) => {


        console.log("System: starting");
        console.log("snapshot: ", snap);
        console.log("snapshot.after: ", snap);
        console.log("snapshot.after.val(): ", snap.val());

        let chatroom_id = context.params.messageId;
        let businessId = snap.val().businessId;
        let userId = snap.val().userId;
        let createdAt = snap.val().createdAt;
        let lastMessage = snap.val().message;

        console.log("CHATROOM ID: ", chatroom_id);
        console.log("BUSINESS ID: ", businessId);
        console.log("USER ID: ", userId);
        console.log("CREATED A: T", createdAt);
        console.log("LAST MESSAGE: ", lastMessage);

        var myDB = admin.database();
        var ref = myDB.ref('users/');
        let tokenUser = "";
        let tokenBusiness = "";

        ref.child(userId).on("value", function (snapshot) {
            tokenUser = snapshot.val().messaging_token;
            console.log("USER TOKEN: ", tokenUser);
        });

        ref.child(businessId).on("value", function (snapshot) {
            tokenBusiness = snapshot.val().messaging_token;
            console.log("BUSINESS TOKEN: ", tokenBusiness);
        });


        var businessName = "";
        var coverUri = "";

        var bRef = myDB.ref('business/');

        return bRef.child(businessId).on("value", function (snapshot) {
            console.log("BUSINESS NAME: ", snapshot.val());
            businessName = snapshot.val().businessName;

            return bRef.child(businessId).on("value", function (snapshot) {
                console.log("BUSINESS IMAGGE: ", snapshot.val());
                coverUri = snapshot.val().coverUri;

                return ref.child(chatroom_id).set({
                    chatroomname: businessName,
                    createdAt: createdAt,
                    creatorId: userId,
                    image: coverUri,
                    inboxImage: coverUri,
                    lastmessage: lastMessage,
                    updatedAt: createdAt,
                    tokens: {
                        userId: {
                            token: tokenUser,
                        },
                        businessId: {
                            token: tokenBusiness,
                        }
                    }
                });
            });
        });
        
    });

exports.newMessage = functions.database.ref('/messages/{chatroomId}/{messageID}/')
    .onWrite((change, context) => {

        console.log("System: starting");
        console.log("snapshot: ", change);
        console.log("snapshot.after: ", change.after);
        console.log("snapshot.after.val(): ", change.after.val());

        //get the message that was written
        let message = change.after.val().message;
        let messageUserId = change.after.val().senderId;
        console.log("message: ", message);
        console.log("senderId: ", messageUserId);

        return change.after.ref.parent.parent.parent.once('value').then(snap => {
            console.log("Snapshot: ", snap);
            let data = snap.child('users').val();
            console.log("data: ", data);
            //get the number of users in the chatroom
            let length = 0;
            for (value in data) {
                length++;
            }
            console.log("data length: ", length);

            //loop through each user currently in the chatroom
            let tokens = [];
            let i = 0;
            for (var user_id in data) {
                console.log("user_id: ", user_id);

                //get the token and add it to the array 
                let reference = admin.database().ref("/users/" + user_id);
                reference.once('value').then(snap => {
                    //get the token
                    let token = snap.child('messaging_token').val();
                    console.log('token: ', token);
                    tokens.push(token);
                    i++;

                    //also check to see if the user_id we're viewing is the user who posted the message
                    //if it is, then save that name so we can pre-pend it to the message
                    let messageUserName = "";
                    if (snap.child('user_id').val() === messageUserId) {
                        messageUserName = snap.child('name').val();
                        console.log("message user name: ", messageUserName);
                        message = messageUserName + ": " + message;
                    }

                    //Once the last user in the list has been added we can continue
                    if (i === length) {

                        console.log("Construction the notification message.");
                        const payload = {

                            data: {
                                data_type: "data_type_chat_message",
                                title: "Pasalubong Hub",
                                message: message,
                                chatroom_id: chatroomId
                            }
                        };


                        return admin.messaging().sendToDevice(tokens, payload)
                            .then(function (response) {
                                // See the MessagingDevicesResponse reference documentation for
                                // the contents of response.
                                console.log("Successfully sent message:", response);
                                return null;
                            })
                            .catch(function (error) {
                                console.log("Error sending message:", error);
                            });
                    }
                    return null;
                }).catch(function (error) {
                    console.log("Error sending message:", error);
                });
            }
            return null;
        }).catch(function (error) {
            console.log("Error sending message:", error);
        });
    });





