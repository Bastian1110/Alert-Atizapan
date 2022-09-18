"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// Import the Firebase Admin to interact with FCM
const admin = require("firebase-admin");
const express = require("express");
const bodyparser = require("body-parser");
const cors = require("cors");
//Initialization of Firebase admin with secret 
let serviceAccount = require("../alerta-atizapan-firebase-adminsdk-f6242-46579323c0.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://alerta-atizapan-default-rtdb.firebaseio.com"
});
//Configuration of express server
const app = express();
const port = process.env.PORT || 5000;
app.use(cors());
app.use(bodyparser.urlencoded({ extended: true }));
app.use(bodyparser.json());
//Main stream of notification (all devices)
const topic = "all";
//Endpoint "addNotification which expects a notification body to be sent to app"
app.post('/addNotification', async (req, res) => {
    const notification = req.body.notification;
    const message = { notification, topic };
    admin.messaging().send(message)
        .then(response => {
        console.log("Successfully sent message:", response);
        res.send({ code: 200, message: "Success!", messageId: response });
    })
        .catch(error => {
        console.log("Error sending message:", error);
        res.send({ code: 500, message: "Panic!", error });
    });
});
//Starting the express server
app.listen(port, () => console.log(`Server is listening on http://localhost/${port}`));
//# sourceMappingURL=index.js.map