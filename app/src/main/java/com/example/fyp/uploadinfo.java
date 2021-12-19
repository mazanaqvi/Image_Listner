package com.example.fyp;

public class uploadinfo {

        public String extracted_text;
        public uploadinfo(){}

        public uploadinfo(String text) {
            this.extracted_text = text;

        }

       public String getImageURL() {
            return extracted_text;
        }

}
