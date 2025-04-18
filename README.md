# Edge-based Human Activity Recognition System for Smart Healthcare

This repository implements a **Human Activity Recognition System** for smart healthcare applications. It classifies sequences of mobile sensor data into specific movements using deep learning models.

The system leverages **Convolutional Neural Networks (CNNs)** and **Recurrent Neural Networks (RNNs)** to achieve accurate recognition of human activity. It also implements multiple CNN and RNN architectures for experimentation.

### Paper Reference:
This work is based on the paper titled **"Edge-based Human Activity Recognition System for Smart Healthcare"** by the following authors:
- **Anirban Mukherjee, Amitrajit Bose, Debdeep Paul Chaudhuri, Akash Kumar, Aiswarya Chatterjee, Saurav Kumar Ray, Anay Ghosh**

**Published at:** *Journal of The Institution of Engineers (India): Series B*  
**Link to the paper:** [Read here](https://link.springer.com/article/10.1007/s40031-021-00663-w)

---

## Models Used

The following models are used for classifying human activity based on mobile sensor data:

### 1. **Convolutional Neural Networks (CNN)**
- Simple CNN
- Depthwise Separable CNN

### 2. **Recurrent Neural Networks (RNN)**
- GRU (Gated Recurrent Unit)
- LSTM (Long Short-Term Memory)
- Pruned GRU

---

## Dataset

The dataset used for training and testing is **Human Activity Recognition (HAR)**, which consists of sensor data collected from smartphones. You can access and download the dataset from Kaggle using the following link:

[Human Activity Recognition Dataset on Kaggle](https://www.kaggle.com/erenaktas/human-activity-recognition)

---

## Requirements

The following libraries are required to run this project:

- **TensorFlow** (for deep learning models)
- **Keras** (for model building and training)
- **NumPy** (for data manipulation)
- **Pandas** (for data handling)
- **Matplotlib** (for plotting and visualizations)
- **Seaborn** (for statistical data visualizations)

---

## License

This repository is open-source and available under the [MIT License](LICENSE).
