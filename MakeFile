# === Variables de configuration ===
ALIAS=api
PASSWORD=Dinamo22**
DOMAIN=api.buy01.site
LETSENCRYPT_DIR=/etc/letsencrypt/live/$(DOMAIN)
KEYSTORE_DIR=backend/services/gateway/src/main/resources/keystore/
KEYSTORE_FILE=$(KEYSTORE_DIR)/keystore.p12

# === Commande Make ===
https:
	@echo "Conversion du certificat Let's Encrypt vers PKCS12..."
	sudo openssl pkcs12 -export \
		-in $(LETSENCRYPT_DIR)/fullchain.pem \
		-inkey $(LETSENCRYPT_DIR)/privkey.pem \
		-out $(KEYSTORE_FILE) \
		-name $(ALIAS) \
		-passout pass:$(PASSWORD)

	@echo "Application des permissions au keystore..."
	sudo chmod 777 $(KEYSTORE_FILE)

	@echo "✅ Keystore généré et permissions appliquées : $(KEYSTORE_FILE)"

push:
	@if [ -z "$(msg)" ]; then echo "Commit message is required"; exit 1; fi
	git add .
	git commit -m "$(msg)"
	git push
