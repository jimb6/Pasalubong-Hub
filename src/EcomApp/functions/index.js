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

// Create a HTTP request cloud function.
exports.sendCollectionToAlgolia = functions.https.onRequest(async (req, res) => {

	// This array will contain all records to be indexed in Algolia.
	// A record does not need to necessarily contain all properties of the Firestore document,
	// only the relevant ones. 
	const algoliaRecords = [];

	// Retrieve all documents from the COLLECTION collection.
	const querySnapshot = await db.collection('products').get();

	querySnapshot.docs.forEach(doc => {
		const document = doc.data();
        // Essentially, you want your records to contain any information that facilitates search, 
        // display, filtering, or relevance. Otherwise, you can leave it out.
        const record = {
            objectID: doc.id,
            businessOwnerId: document.businessOwnerId,
            productname: document.productname,
			productDescription: document.productDescription,
            imageUrls: document.imageUrls,
            condition: document.condition,
            isNew: document.isNew,
            price: document.price,
            productCategory: document.productCategory,
            stock: document.stock,
            wholeSeller: document.wholeSeller
        };

        algoliaRecords.push(record);
    });
	
	// After all records are created, we save them to 
	collectionIndex.saveObjects(algoliaRecords, (_error, content) => {
        res.status(200).send("COLLECTION was indexed to Algolia successfully.");
    });
	
});

exports.collectionOnCreate = functions.firestore.document('products/{uid}').onCreate(async (snapshot, context) => {
    await saveDocumentInAlgolia(snapshot);
});

exports.collectionOnUpdate = functions.firestore.document('products/{uid}').onUpdate(async (change, context) => {
    await updateDocumentInAlgolia(change);
});

exports.collectionOnDelete = functions.firestore.document('products/{uid}').onDelete(async (snapshot, context) => {
    await deleteDocumentFromAlgolia(snapshot);
});




async function saveDocumentInAlgolia(snapshot) {
    if (snapshot.exists) {
        const record = snapshot.data();
        if (record) { // Removes the possibility of snapshot.data() being undefined.
            if (record.isIncomplete === false) { // We only index products that are complete.
                record.objectID = snapshot.id;
                
                // In this example, we are including all properties of the Firestore document 
                // in the Algolia record, but do remember to evaluate if they are all necessary.
                // More on that in Part 2, Step 2 above.
                
                await collectionIndex.saveObject(record); // Adds or replaces a specific object.
            }
        }
    }
}

async function updateDocumentInAlgolia(change) {
    const docBeforeChange = change.before.data()
    const docAfterChange = change.after.data()
    if (docBeforeChange && docAfterChange) {
        if (docAfterChange.isIncomplete && !docBeforeChange.isIncomplete) {
            // If the doc was COMPLETE and is now INCOMPLETE, it was 
            // previously indexed in algolia and must now be removed.
            await deleteDocumentFromAlgolia(change.after);
        } else if (docAfterChange.isIncomplete === false) {
            await saveDocumentInAlgolia(change.after);
        }
    }
}

async function deleteDocumentFromAlgolia(snapshot) {
    if (snapshot.exists) {
        const objectID = snapshot.id;
        await collectionIndex.deleteObject(objectID);
    }
}



exports.sendNotification = functions.database.ref('/chatrooms/{chatroomId}/chatroom_messages/{chatmessageId}')
    .onWrite((snap, context) => {

        console.log("System: starting");
        console.log("snapshot: ", snap);
        console.log("snapshot.after: ", snap.after);
        console.log("snapshot.after.val(): ", snap.after.val());

        //get the message that was written
        let message = snap.after.val().message;
        let messageUserId = snap.after.val().user_id;
        console.log("message: ", message);
        console.log("user_id: ", messageUserId);

        //get the chatroom id
        let chatroomId = context.params.chatroomId;
        console.log("chatroom_id: ", chatroomId);

        return snap.after.ref.parent.parent.once('value').then(snap => {
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
                                title: "Tabian Consulting",
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
