export interface Media {
  id: string;
  imagePath: string;
  productId: string;
  fileName: string;
  cloudId: string;
  fileSize: number;
  mimeType: string;
  uploadDate: string;
}

export interface UploadMediaResponse {
  id: string;
  url: string;
  fileName: string;
}

export interface MediaRequeste {
  imagePath: string;
  productId: string;
  fileName: string;
  cloudId: string;
  fileSize: number;
  mimeType: string;
  uploadDate: string;
}