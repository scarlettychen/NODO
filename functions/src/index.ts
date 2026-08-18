import {setGlobalOptions} from "firebase-functions";
import {onRequest} from "firebase-functions/https";
import * as logger from "firebase-functions/logger";
import {initializeApp} from "firebase-admin/app";
import {FieldValue, getFirestore} from "firebase-admin/firestore";

initializeApp();
setGlobalOptions({maxInstances: 10});

/**
 * Anonymous library adoption ping. Stores only library name, version, and a timestamp.
 * Does not read IP addresses, robot IDs, or any other request metadata.
 */
export const trackUsage = onRequest({invoker: "public"}, async (req, res) => {
  res.set("Access-Control-Allow-Origin", "*");
  res.set("Access-Control-Allow-Methods", "POST, OPTIONS");
  res.set("Access-Control-Allow-Headers", "Content-Type");

  if (req.method === "OPTIONS") {
    res.status(204).send("");
    return;
  }

  if (req.method !== "POST") {
    res.status(405).send("Method Not Allowed");
    return;
  }

  try {
    const library = typeof req.body?.library === "string" ? req.body.library : "";
    const version = typeof req.body?.version === "string" ? req.body.version : "";

    if (!library || !version) {
      res.status(400).send("Missing fields");
      return;
    }

    const db = getFirestore();
    const ref = db.collection("usage").doc("NODO");

    await db.runTransaction(async (transaction) => {
      const doc = await transaction.get(ref);

      if (!doc.exists) {
        transaction.set(ref, {
          library: "NODO",
          total: 1,
          versions: {[version]: 1},
          last_seen: FieldValue.serverTimestamp(),
        });
      } else {
        const data = doc.data();
        const versions = data?.versions || {};
        versions[version] = (versions[version] || 0) + 1;

        transaction.update(ref, {
          library: "NODO",
          total: FieldValue.increment(1),
          versions,
          last_seen: FieldValue.serverTimestamp(),
        });
      }
    });

    res.status(200).send("ok");
  } catch (error) {
    logger.error("trackUsage failed");
    res.status(500).send("error");
  }
});
