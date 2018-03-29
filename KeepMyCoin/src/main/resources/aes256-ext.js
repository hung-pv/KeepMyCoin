var encryptAES = function(key, buffer) {
	// The counter is optional, and if omitted will begin at 1
	var aesCtr = new aesjs.ModeOfOperation.ctr(key, new aesjs.Counter(5));
	var encryptedBytes = aesCtr.encrypt(buffer);
	// To print or store the binary data, you may convert it to hex
	var encryptedHex = aesjs.utils.hex.fromBytes(encryptedBytes);
	return encryptedHex;
}

var decryptAES = function(key, encryptedBuffer) {
	// The counter mode of operation maintains internal state, so to
	// decrypt a new instance must be instantiated.
	var aesCtr = new aesjs.ModeOfOperation.ctr(key, new aesjs.Counter(5));
	var decryptedBytes = aesCtr.decrypt(encryptedBuffer);
	// Convert our bytes back into text
	var decryptedText = aesjs.utils.utf8.fromBytes(decryptedBytes);
	return decryptedText;
}