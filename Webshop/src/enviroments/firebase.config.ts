// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyAxOtOdHiI5RHyP7UYAToaX3Ry0kjiBSQc",
  authDomain: "fluffy-plushies-webshop.firebaseapp.com",
  projectId: "fluffy-plushies-webshop",
  storageBucket: "fluffy-plushies-webshop.appspot.com",
  messagingSenderId: "827529865010",
  appId: "1:827529865010:web:711fe366ec98902203992b",
  measurementId: "G-YEXEJZ9G6M"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

export { storage };
