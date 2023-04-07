MenuItem uploadItem = new MenuItem("Upload Songs");
uploadItem.setOnAction(event -> {
	    FileUploadUI uploadUI = new FileUploadUI();
	        uploadUI.start(new Stage());
});
fileMenu.getItems().add(uploadItem);

