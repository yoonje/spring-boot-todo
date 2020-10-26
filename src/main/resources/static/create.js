$('#create-submit').on('click', createToDo);

function createToDo() {
    var description = $('#create-description').val();
    if (!description.trim()) {
        alert('내용을 입력해주세요');
        return;
    }
    var reference = $('#create-references').val();
    var references = []
    try {
        references = parse(reference);
    } catch (e) {
        alert(e);
        return;
    }
    var todo = {'description': description, 'references': references, 'status': 'open'};
    $.ajax({
        url: '/todos',
        type: 'POST',
        contentType: 'application/json;utf-8',
        data: JSON.stringify(todo),
        success: function () {
            alert('처리되었습니다.');
            $('#delete-modal').modal('hide');
            location.href = '/todos';
        },
        error: function(xhr, status, error) {
            if (xhr.status >= 500) {
                alert('처리에 실패 했습니다.');
            } else {
                alert(xhr.responseJSON.message);
            }
            console.log(arguments);
        }
    });
}