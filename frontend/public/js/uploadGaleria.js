document.addEventListener('DOMContentLoaded', function() {
    // Elementos do DOM
    const dropZone = document.querySelector('.drop-zone');
    const fileInput = document.getElementById('mediaFiles');
    const filePreview = document.getElementById('filePreview');
    const uploadForm = document.getElementById('uploadForm');
    const submitBtn = document.getElementById('submitBtn');
    const uploadsList = document.getElementById('uploadsList');
    
    // Array para armazenar os arquivos selecionados
    let selectedFiles = [];
    
    // Simulação de uploads recentes (substituir por chamada ao banco de dados)
    const recentUploads = [
        { id: 1, type: 'image', url: '/images/futebol.jpg', eventType: 'Futebol', date: '2023-05-15' },
        { id: 2, type: 'video', url: '/videos/tenis.mp4', eventType: 'Tênis', date: '2023-05-10' },
        { id: 3, type: 'image', url: '/images/maratona.jpg', eventType: 'Maratona', date: '2023-05-05' }
    ];
    
    // Carrega uploads recentes
    loadRecentUploads();
    
    // Event listeners para a área de drop
    dropZone.addEventListener('click', () => fileInput.click());
    
    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.style.backgroundColor = 'rgba(231, 125, 0, 0.2)';
    });
    
    dropZone.addEventListener('dragleave', () => {
        dropZone.style.backgroundColor = 'rgba(231, 125, 0, 0.05)';
    });
    
    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropZone.style.backgroundColor = 'rgba(231, 125, 0, 0.05)';
        
        if (e.dataTransfer.files.length) {
            fileInput.files = e.dataTransfer.files;
            handleFiles(e.dataTransfer.files);
        }
    });
    
    fileInput.addEventListener('change', () => {
        if (fileInput.files.length) {
            handleFiles(fileInput.files);
        }
    });
    
    // Form submit
    uploadForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (selectedFiles.length === 0) {
            alert('Por favor, selecione pelo menos um arquivo.');
            return;
        }
        
        submitBtn.disabled = true;
        submitBtn.textContent = 'Enviando...';
        
        try {
            // Simulação de envio para o servidor
            await uploadFiles();
            
            alert('Arquivos enviados com sucesso!');
            selectedFiles = [];
            filePreview.innerHTML = '';
            loadRecentUploads();
        } catch (error) {
            console.error('Erro no upload:', error);
            alert('Ocorreu um erro ao enviar os arquivos. Por favor, tente novamente.');
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Enviar Arquivos';
        }
    });
    
    // Função para lidar com os arquivos selecionados
    function handleFiles(files) {
        selectedFiles = Array.from(files);
        filePreview.innerHTML = '';
        
        selectedFiles.forEach((file, index) => {
            const previewItem = document.createElement('div');
            previewItem.className = 'preview-item';
            
            if (file.type.startsWith('image/')) {
                const img = document.createElement('img');
                img.src = URL.createObjectURL(file);
                previewItem.appendChild(img);
            } else if (file.type.startsWith('video/')) {
                const video = document.createElement('video');
                video.src = URL.createObjectURL(file);
                video.controls = true;
                previewItem.appendChild(video);
            } else {
                // Se não for imagem nem vídeo, mostra ícone
                previewItem.textContent = file.name;
                previewItem.style.display = 'flex';
                previewItem.style.alignItems = 'center';
                previewItem.style.justifyContent = 'center';
                previewItem.style.backgroundColor = '#f0f0f0';
            }
            
            const removeBtn = document.createElement('button');
            removeBtn.className = 'remove-btn';
            removeBtn.innerHTML = '&times;';
            removeBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                selectedFiles.splice(index, 1);
                filePreview.removeChild(previewItem);
            });
            
            previewItem.appendChild(removeBtn);
            filePreview.appendChild(previewItem);
        });
    }
    
    // Função para simular upload (substituir por chamada real à API)
    function uploadFiles() {
        return new Promise((resolve) => {
            // Aqui você faria a chamada real para o backend
            // Exemplo com fetch:
            /*
            const formData = new FormData();
            selectedFiles.forEach(file => {
                formData.append('mediaFiles', file);
            });
            formData.append('eventType', document.getElementById('eventType').value);
            formData.append('eventDate', document.getElementById('eventDate').value);
            
            fetch('/api/upload', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => resolve(data))
            .catch(error => reject(error));
            */
            
            // Simulando delay de rede
            setTimeout(() => {
                resolve({ success: true });
            }, 1500);
        });
    }
    
    // Função para carregar uploads recentes (substituir por chamada real ao banco de dados)
    function loadRecentUploads() {
        uploadsList.innerHTML = '';
        
        recentUploads.forEach(upload => {
            const item = document.createElement('div');
            item.className = 'upload-item';
            
            if (upload.type === 'image') {
                const img = document.createElement('img');
                img.src = upload.url;
                img.alt = upload.eventType;
                item.appendChild(img);
            } else {
                const video = document.createElement('video');
                video.src = upload.url;
                video.controls = true;
                item.appendChild(video);
            }
            
            const info = document.createElement('div');
            info.className = 'upload-item-info';
            info.innerHTML = `
                <h4>${upload.eventType}</h4>
                <p>${formatDate(upload.date)}</p>
            `;
            
            item.appendChild(info);
            uploadsList.appendChild(item);
        });
    }
    
    // Função auxiliar para formatar data
    function formatDate(dateString) {
        const options = { year: 'numeric', month: 'short', day: 'numeric' };
        return new Date(dateString).toLocaleDateString('pt-BR', options);
    }
});